import { useEffect, useState } from 'react';
import { Button, Table, Space, Tag, Modal, Form, Input, InputNumber, message } from 'antd';
import { PlusOutlined, PlayCircleOutlined, StopOutlined, EyeOutlined, EditOutlined, LogoutOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';

export default function DashboardPage() {
  const [competitions, setCompetitions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [createVisible, setCreateVisible] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const role = localStorage.getItem('role');

  const fetchList = async () => {
    setLoading(true);
    try {
      const url = role === 'PARTICIPANT' ? '/participant/competitions' : '/competitions';
      const { data } = await client.get(url);
      setCompetitions(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchList(); }, []);

  const handleCreate = async (values: any) => {
    try {
      const maxRank = values.maxRank;
      const totalParticipants = values.totalParticipants || 0;
      const rankConfigs = Array.from({ length: maxRank }, (_, i) => ({
        rankNumber: i + 1,
        capacity: i + 1 === maxRank ? 0 : (values[`capacity_${i + 1}`] || 1)
      }));
      await client.post('/competitions', {
        name: values.name,
        description: values.description,
        maxRank: maxRank,
        totalParticipants: totalParticipants,
        scoreModifyWindowMinutes: values.scoreModifyWindowMinutes || 10,
        rankConfigs: rankConfigs
      });
      message.success('比赛创建成功');
      setCreateVisible(false);
      form.resetFields();
      fetchList();
    } catch (err: any) {
      message.error(err.response?.data?.message || '创建失败');
    }
  };

  const handleStart = async (id: number) => {
    await client.put(`/competitions/${id}/start`);
    message.success('比赛已开始');
    fetchList();
  };

  const handleFinish = async (id: number) => {
    Modal.confirm({
      title: '确认结束比赛?',
      content: '结束后将生成最终排名，不可再修改评分。',
      onOk: async () => {
        await client.put(`/competitions/${id}/finish`);
        message.success('比赛已结束');
        fetchList();
      }
    });
  };

  const logout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const statusMap: Record<string, { color: string; text: string }> = {
    DRAFT: { color: 'default', text: '草稿' },
    SCORING: { color: 'processing', text: '评分中' },
    FINISHED: { color: 'success', text: '已结束' }
  };

  const roleLabelMap: Record<string, string> = {
    ADMIN: '管理员',
    JUDGE: '评委',
    PARTICIPANT: '选手'
  };

  const columns = [
    { title: '比赛名称', dataIndex: 'name', key: 'name' },
    { title: '状态', dataIndex: 'status', key: 'status', render: (s: string) => {
      const info = statusMap[s];
      return <Tag color={info?.color}>{info?.text || s}</Tag>;
    }},
    { title: '名次数', dataIndex: 'maxRank', key: 'maxRank' },
    { title: '总参赛人数', dataIndex: 'totalParticipants', key: 'totalParticipants' },
    { title: '参评人数', dataIndex: 'participantCount', key: 'participantCount' },
    { title: '评委数', dataIndex: 'judgeCount', key: 'judgeCount' },
    { title: '创建者', dataIndex: 'createdByName', key: 'createdByName' },
    {
      title: '操作', key: 'actions',
      render: (_: any, record: any) => (
        <Space>
          {record.status === 'DRAFT' && role === 'ADMIN' && (
            <>
              <Button size="small" icon={<EditOutlined />}
                onClick={() => navigate(`/competition/${record.id}/setup`)}>配置</Button>
              <Button size="small" type="primary" icon={<PlayCircleOutlined />}
                onClick={() => handleStart(record.id)}>开始</Button>
            </>
          )}
          {record.status === 'SCORING' && role === 'ADMIN' && (
            <Button size="small" danger icon={<StopOutlined />}
              onClick={() => handleFinish(record.id)}>结束</Button>
          )}
          {record.status === 'SCORING' && role === 'JUDGE' && (
            <Button size="small" type="primary"
              onClick={() => navigate(`/competition/${record.id}/score`)}>评分</Button>
          )}
          {record.status !== 'DRAFT' && role === 'PARTICIPANT' && (
            <Button size="small" type="primary"
              onClick={() => navigate(`/competition/${record.id}/participant`)}>我的作品</Button>
          )}
          {record.status === 'FINISHED' && (
            <Button size="small" icon={<EyeOutlined />}
              onClick={() => navigate(`/competition/${record.id}/report`)}>查看报表</Button>
          )}
          {record.status === 'DRAFT' && (role === 'JUDGE' || role === 'PARTICIPANT') && <span>-</span>}
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h1>多媒体作品评审系统</h1>
        <Space>
          <span>{localStorage.getItem('name')} ({roleLabelMap[role || ''] || role})</span>
          {role === 'ADMIN' && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateVisible(true)}>创建比赛</Button>
          )}
          <Button icon={<LogoutOutlined />} onClick={logout}>退出</Button>
        </Space>
      </div>

      <Table columns={columns} dataSource={competitions} rowKey="id" loading={loading} />

      <Modal title="创建比赛" open={createVisible} onCancel={() => setCreateVisible(false)}
        onOk={() => form.submit()} width={600}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="name" label="比赛名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="maxRank" label="名次数量" rules={[{ required: true }]}
            extra="最多10名">
            <InputNumber min={1} max={10} />
          </Form.Item>
          <Form.Item name="totalParticipants" label="总参赛人数" rules={[{ required: true }]}
            extra="最后一名次名额将自动计算">
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item name="scoreModifyWindowMinutes" label="评分修改时限(分钟)" initialValue={10}>
            <InputNumber min={1} max={60} />
          </Form.Item>
          <Form.Item noStyle shouldUpdate={(prev, cur) => prev.maxRank !== cur.maxRank || prev.totalParticipants !== cur.totalParticipants}>
            {({ getFieldValue }) => {
              const maxRank = getFieldValue('maxRank') || 0;
              const totalParticipants = getFieldValue('totalParticipants') || 0;
              if (maxRank === 0) return null;
              return Array.from({ length: maxRank }, (_, i) => {
                const rankNumber = i + 1;
                const isLast = rankNumber === maxRank;
                // Calculate remaining capacity for last rank
                let reserved = 0;
                for (let r = 1; r < maxRank; r++) {
                  reserved += getFieldValue(`capacity_${r}`) || 0;
                }
                const autoCapacity = totalParticipants - reserved;
                return (
                  <Form.Item key={i} name={isLast ? undefined : `capacity_${rankNumber}`}
                    label={isLast ? `第${rankNumber}名名额 (自动)` : `第${rankNumber}名名额`}
                    rules={isLast ? [] : [{ required: true }]}>
                    {isLast ? (
                      <InputNumber disabled value={autoCapacity > 0 ? autoCapacity : 0}
                        style={{ width: '100%', color: autoCapacity <= 0 ? '#ff4d4f' : undefined }} />
                    ) : (
                      <InputNumber min={1} />
                    )}
                  </Form.Item>
                );
              });
            }}
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
