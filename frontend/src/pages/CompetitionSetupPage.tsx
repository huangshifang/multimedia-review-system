import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tabs, Table, Button, Modal, Form, Input, Upload, message, Space, Popconfirm, Select } from 'antd';
import { PlusOutlined, UploadOutlined, DeleteOutlined, ArrowLeftOutlined, UserAddOutlined } from '@ant-design/icons';
import client from '../api/client';

export default function CompetitionSetupPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [participants, setParticipants] = useState<any[]>([]);
  const [judges, setJudges] = useState<any[]>([]);
  const [allJudges, setAllJudges] = useState<any[]>([]);
  const [participantUsers, setParticipantUsers] = useState<any[]>([]);
  const [addVisible, setAddVisible] = useState(false);
  const [addJudgeVisible, setAddJudgeVisible] = useState(false);
  const [form] = Form.useForm();
  const [judgeForm] = Form.useForm();

  const fetchParticipants = async () => {
    const { data } = await client.get(`/competitions/${id}/participants`);
    setParticipants(data);
  };

  const fetchJudges = async () => {
    const { data } = await client.get(`/competitions/${id}/judges`);
    setJudges(data);
  };

  const fetchAllJudges = async () => {
    try {
      const { data } = await client.get('/users?role=JUDGE');
      setAllJudges(data);
    } catch {
      setAllJudges([]);
    }
  };

  const fetchParticipantUsers = async () => {
    try {
      const { data } = await client.get('/users?role=PARTICIPANT');
      setParticipantUsers(data);
    } catch {
      setParticipantUsers([]);
    }
  };

  useEffect(() => {
    fetchParticipants();
    fetchJudges();
    fetchAllJudges();
    fetchParticipantUsers();
  }, [id]);

  const handleAddParticipant = async (values: any) => {
    await client.post(`/competitions/${id}/participants`, values);
    message.success('参评人添加成功');
    setAddVisible(false);
    form.resetFields();
    fetchParticipants();
  };

  const handleDeleteParticipant = async (pid: number) => {
    await client.delete(`/competitions/${id}/participants/${pid}`);
    message.success('删除成功');
    fetchParticipants();
  };

  const handleUploadFile = async (pid: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    await client.post(`/competitions/${id}/participants/${pid}/files`, formData);
    message.success('文件上传成功');
    fetchParticipants();
    return false;
  };

  const handleAssignJudges = async (values: any) => {
    await client.post(`/competitions/${id}/judges`, { userIds: values.userIds });
    message.success('评委分配成功');
    setAddJudgeVisible(false);
    judgeForm.resetFields();
    fetchJudges();
  };

  const handleRemoveJudge = async (jid: number) => {
    await client.delete(`/competitions/${id}/judges/${jid}`);
    message.success('评委移除成功');
    fetchJudges();
  };

  const participantColumns = [
    { title: '姓名', dataIndex: 'name', key: 'name' },
    { title: '部门/描述', dataIndex: 'department', key: 'department' },
    { title: '关联账号', dataIndex: 'linkedUsername', key: 'linkedUsername',
      render: (v: string) => v || <span style={{ color: '#999' }}>未关联</span> },
    {
      title: '作品文件', key: 'files',
      render: (_: any, record: any) => (
        <Space direction="vertical" size="small">
          {record.files?.map((f: any) => (
            <a key={f.id} href={f.downloadUrl} target="_blank" rel="noopener">
              [{f.fileType}] {f.originalName} ({(f.fileSize / 1024 / 1024).toFixed(1)}MB)
            </a>
          ))}
          <Upload showUploadList={false}
            accept=".txt,.doc,.docx,.pdf,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.bmp,.webp,.svg,.mp4,.mov,.avi,.mkv,.wmv,.flv,.webm,.mp3,.wav,.aac,.flac,.ogg,.wma"
            beforeUpload={(file) => { handleUploadFile(record.id, file); return false; }}>
            <Button size="small" icon={<UploadOutlined />}>上传作品</Button>
          </Upload>
        </Space>
      )
    },
    {
      title: '操作', key: 'actions',
      render: (_: any, record: any) => (
        <Popconfirm title="确认删除?" onConfirm={() => handleDeleteParticipant(record.id)}>
          <Button size="small" danger icon={<DeleteOutlined />} />
        </Popconfirm>
      )
    }
  ];

  const judgeColumns = [
    { title: '姓名', dataIndex: 'name', key: 'name' },
    { title: '用户名', dataIndex: 'username', key: 'username' },
    {
      title: '操作', key: 'actions',
      render: (_: any, record: any) => (
        <Popconfirm title="确认移除?" onConfirm={() => handleRemoveJudge(record.id)}>
          <Button size="small" danger>移除</Button>
        </Popconfirm>
      )
    }
  ];

  return (
    <div style={{ padding: 24, maxWidth: 1000, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')}>返回</Button>
        <h2 style={{ margin: 0 }}>比赛配置</h2>
      </Space>

      <Tabs items={[
        {
          key: 'participants',
          label: `参评人 (${participants.length})`,
          children: (
            <>
              <Button type="primary" icon={<PlusOutlined />} style={{ marginBottom: 16 }}
                onClick={() => setAddVisible(true)}>添加参评人</Button>
              <Table columns={participantColumns} dataSource={participants} rowKey="id" />
            </>
          )
        },
        {
          key: 'judges',
          label: `评委 (${judges.length})`,
          children: (
            <>
              <Button type="primary" icon={<UserAddOutlined />} style={{ marginBottom: 16 }}
                onClick={() => setAddJudgeVisible(true)}>分配评委</Button>
              <Table columns={judgeColumns} dataSource={judges} rowKey="id" />
            </>
          )
        }
      ]} />

      <Modal title="添加参评人" open={addVisible} onCancel={() => setAddVisible(false)}
        onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleAddParticipant}>
          <Form.Item name="name" label="姓名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="department" label="部门/描述">
            <Input />
          </Form.Item>
          <Form.Item name="userId" label="关联选手账号">
            <Select placeholder="选择关联的选手账号（可空）" allowClear options={
              participantUsers.map((u: any) => ({ label: `${u.name} (${u.username})`, value: u.id }))
            } />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="分配评委" open={addJudgeVisible} onCancel={() => setAddJudgeVisible(false)}
        onOk={() => judgeForm.submit()}>
        <Form form={judgeForm} layout="vertical" onFinish={handleAssignJudges}>
          <Form.Item name="userIds" label="选择评委" rules={[{ required: true }]}>
            <Select mode="multiple" placeholder="选择评委用户" options={
              allJudges.map((j: any) => ({ label: `${j.name} (${j.username})`, value: j.id }))
            } />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
