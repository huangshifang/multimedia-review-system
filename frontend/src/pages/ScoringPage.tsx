import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Form, InputNumber, Button, message, Space, Tag, Modal, Spin } from 'antd';
import { ArrowLeftOutlined, SendOutlined, EyeOutlined } from '@ant-design/icons';
import client from '../api/client';

export default function ScoringPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [competition, setCompetition] = useState<any>(null);
  const [participants, setParticipants] = useState<any[]>([]);
  const [myScores, setMyScores] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [previewFile, setPreviewFile] = useState<{ url: string; name: string } | null>(null);

  const fetchData = async () => {
    const [compRes, partRes, scoreRes] = await Promise.all([
      client.get(`/competitions/${id}`),
      client.get(`/competitions/${id}/participants`),
      client.get(`/competitions/${id}/my-scores`)
    ]);
    setCompetition(compRes.data);
    setParticipants(partRes.data);
    setMyScores(scoreRes.data);
  };

  useEffect(() => { fetchData(); }, [id]);

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const scores = Object.entries(values).map(([key, score]) => ({
        participantId: Number(key.replace('score_', '')),
        score
      }));
      await client.post(`/competitions/${id}/scores`, { scores });
      message.success('评分提交成功');
      fetchData();
    } catch (err: any) {
      message.error(err.response?.data?.message || '提交失败');
    } finally {
      setLoading(false);
    }
  };

  const getMyScore = (participantId: number) => {
    return myScores.find((s: any) => s.participantId === participantId);
  };

  if (!competition) return <Spin size="large" style={{ display: 'block', margin: '200px auto' }} />;

  const statusMap: Record<string, { color: string; text: string }> = {
    DRAFT: { color: 'default', text: '草稿' },
    SCORING: { color: 'processing', text: '评分中' },
    FINISHED: { color: 'success', text: '已结束' }
  };

  return (
    <div style={{ padding: 24, maxWidth: 900, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')}>返回</Button>
        <h2 style={{ margin: 0 }}>{competition.name} - 评分</h2>
        <Tag color={statusMap[competition.status]?.color}>
          {statusMap[competition.status]?.text}
        </Tag>
      </Space>

      <Form onFinish={handleSubmit} layout="vertical">
        {participants.map((p: any) => {
          const existing = getMyScore(p.id);
          return (
            <Card key={p.id} style={{ marginBottom: 12 }}
              title={
                <Space>
                  <span>{p.name}</span>
                  {p.department && <Tag>{p.department}</Tag>}
                  {existing && (
                    <Tag color={existing.status === 'LOCKED' ? 'red' : 'blue'}>
                      {existing.status === 'LOCKED' ? '已锁定' : '已提交(可修改)'}
                    </Tag>
                  )}
                </Space>
              }
              extra={
                <Space>
                  {p.files?.map((f: any) => (
                    <Button key={f.id} size="small" icon={<EyeOutlined />}
                      onClick={() => setPreviewFile({ url: f.downloadUrl, name: f.originalName })}>
                      [{f.fileType}] {f.originalName}
                    </Button>
                  ))}
                </Space>
              }>
              <Form.Item name={`score_${p.id}`}
                rules={[{ required: true, message: '请打分' }]}
                initialValue={existing?.score}>
                <InputNumber min={0} max={100} step={0.1}
                  style={{ width: 200 }}
                  disabled={existing?.status === 'LOCKED'}
                  addonAfter="分" />
              </Form.Item>
            </Card>
          );
        })}
        {participants.length > 0 && (
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}
              icon={<SendOutlined />} size="large" block
              disabled={competition.status !== 'SCORING'}>
              提交评分
            </Button>
          </Form.Item>
        )}
      </Form>

      <Modal title={`预览: ${previewFile?.name || ''}`} open={!!previewFile}
        onCancel={() => setPreviewFile(null)} footer={null} width={800}>
        {previewFile && (
          previewFile.name.match(/\.(mp4|mov|avi|mkv|wmv|webm)$/i)
            ? <video controls style={{ width: '100%' }} src={previewFile.url}>
                <source src={previewFile.url} />
              </video>
            : previewFile.name.match(/\.(mp3|wav|aac|flac|ogg)$/i)
              ? <audio controls style={{ width: '100%' }} src={previewFile.url} />
              : previewFile.name.match(/\.(jpg|jpeg|png|gif|webp|svg|bmp)$/i)
                ? <img src={previewFile.url} alt={previewFile.name}
                    style={{ maxWidth: '100%', maxHeight: '70vh', display: 'block', margin: '0 auto' }} />
                : <iframe src={previewFile.url} style={{ width: '100%', height: 500, border: 'none' }}
                    title={previewFile.name} />
        )}
      </Modal>
    </div>
  );
}
