import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button, Card, Upload, message, Space, Popconfirm, Empty, Spin } from 'antd';
import { UploadOutlined, DownloadOutlined, DeleteOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import client from '../api/client';

export default function ParticipantWorkspacePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [entries, setEntries] = useState<any[]>([]);
  const [competition, setCompetition] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [compRes, entriesRes] = await Promise.all([
        client.get(`/competitions/${id}`),
        client.get(`/participant/competitions/${id}/entries`)
      ]);
      setCompetition(compRes.data);
      setEntries(entriesRes.data);
    } catch (err: any) {
      message.error(err.response?.data?.message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, [id]);

  const handleUpload = async (participantId: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    try {
      await client.post(`/participant/competitions/${id}/files`, formData);
      message.success('文件上传成功');
      fetchData();
    } catch (err: any) {
      message.error(err.response?.data?.message || '上传失败');
    }
    return false;
  };

  const handleDeleteFile = async (fileId: number) => {
    try {
      await client.delete(`/participant/files/${fileId}`);
      message.success('文件删除成功');
      fetchData();
    } catch (err: any) {
      message.error(err.response?.data?.message || '删除失败');
    }
  };

  const formatSize = (bytes: number) => {
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB';
    return (bytes / 1024 / 1024).toFixed(1) + 'MB';
  };

  if (loading) return <div style={{ padding: 100, textAlign: 'center' }}><Spin size="large" /></div>;

  return (
    <div style={{ padding: 24, maxWidth: 800, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')}>返回</Button>
        <h2 style={{ margin: 0 }}>{competition?.name || '我的作品'}</h2>
      </Space>

      {entries.length === 0 ? (
        <Empty description="你在该比赛中没有参评条目，请联系管理员添加" />
      ) : (
        entries.map((entry: any) => (
          <Card
            key={entry.id}
            title={`${entry.name}${entry.department ? ' - ' + entry.department : ''}`}
            style={{ marginBottom: 16 }}
          >
            {entry.files && entry.files.length > 0 ? (
              <Space direction="vertical" style={{ width: '100%' }}>
                {entry.files.map((f: any) => (
                  <div key={f.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>
                      [{f.fileType}] {f.originalName} ({formatSize(f.fileSize)})
                    </span>
                    <Space>
                      <Button size="small" icon={<DownloadOutlined />}
                        onClick={() => window.open(f.downloadUrl, '_blank')}>下载</Button>
                      <Popconfirm title="确认删除?" onConfirm={() => handleDeleteFile(f.id)}>
                        <Button size="small" danger icon={<DeleteOutlined />} />
                      </Popconfirm>
                    </Space>
                  </div>
                ))}
              </Space>
            ) : (
              <Empty description="暂无作品" image={Empty.PRESENTED_IMAGE_SIMPLE} />
            )}
            <div style={{ marginTop: 12 }}>
              <Upload showUploadList={false}
                accept=".txt,.doc,.docx,.pdf,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.bmp,.webp,.svg,.mp4,.mov,.avi,.mkv,.wmv,.flv,.webm,.mp3,.wav,.aac,.flac,.ogg,.wma"
                beforeUpload={(file) => { handleUpload(entry.id, file); return false; }}>
                <Button type="primary" icon={<UploadOutlined />}>上传作品</Button>
              </Upload>
            </div>
          </Card>
        ))
      )}
    </div>
  );
}
