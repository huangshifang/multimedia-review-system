import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Collapse, Table, Tag, Button, Space, Spin } from 'antd';
import { ArrowLeftOutlined, TrophyOutlined, PrinterOutlined } from '@ant-design/icons';
import client from '../api/client';

const rankColors = ['#ffd700', '#c0c0c0', '#cd7f32', '#1890ff', '#52c41a',
  '#722ed1', '#eb2f96', '#fa8c16', '#13c2c2', '#2f54eb'];

export default function ReportPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [report, setReport] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const { data } = await client.get(`/competitions/${id}/report`);
        setReport(data);
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  if (loading) return <Spin size="large" style={{ display: 'block', margin: '200px auto' }} />;
  if (!report) return null;

  const openPrintView = () => {
    const w = window.open('', '_blank', 'width=900,height=700');
    if (!w) return;
    const rankRows = report.ranks?.flatMap((r: any) => {
      const parts = r.participants?.length
        ? r.participants.map((p: any) => {
            const scores = (p.judgeScores || []).map((s: any) => s.score);
            const highest = scores.length ? Math.max(...scores) : '-';
            const lowest = scores.length ? Math.min(...scores) : '-';
            return `
          <tr>
            <td style="text-align:center">${r.rankNumber}</td>
            <td style="text-align:center">${r.rankLabel}</td>
            <td>${p.participantName}</td>
            <td>${p.department || ''}</td>
            <td style="text-align:center">${highest}</td>
            <td style="text-align:center">${lowest}</td>
            <td style="text-align:center;font-weight:bold">${p.averageScore}</td>
          </tr>`;
          })
        : [];
      // Rank summary row
      const avgScores = (r.participants || [])
        .map((p: any) => parseFloat(p.averageScore) || 0)
        .filter((s: number) => s > 0);
      const rankHigh = avgScores.length ? Math.max(...avgScores).toFixed(1) : '-';
      const rankLow = avgScores.length ? Math.min(...avgScores).toFixed(1) : '-';
      const summaryRow = parts.length ? `
          <tr style="background:#fffbe6;font-weight:bold">
            <td colspan="4" style="text-align:right;font-size:13px">
              第${r.rankNumber}名 · ${r.participants.length}人 &nbsp;|&nbsp;
              最高平均分: <span style="color:#cf1322">${rankHigh}</span> &nbsp;|&nbsp;
              最低平均分: <span style="color:#1890ff">${rankLow}</span>
            </td>
            <td colspan="3" style="text-align:center;font-size:12px;color:#666">分数区间: ${rankLow} ~ ${rankHigh}</td>
          </tr>` : '';
      const emptyRow = !r.participants?.length ? `
          <tr>
            <td style="text-align:center">${r.rankNumber}</td>
            <td style="text-align:center">${r.rankLabel}</td>
            <td colspan="5" style="color:#999;text-align:center">虚位以待</td>
          </tr>` : '';
      return [...parts, summaryRow, emptyRow].filter(Boolean).join('');
    }).join('');
    const now = new Date().toLocaleString('zh-CN');
    const html = `<!DOCTYPE html><html><head><meta charset="UTF-8"><title>排名表 - ${report.competitionName}</title>
<style>
  body { font-family: "Microsoft YaHei","SimHei",sans-serif; padding: 30px; color: #222; }
  .header { text-align: center; margin-bottom: 24px; }
  .header .sysname { font-size: 13px; color: #888; margin-bottom: 12px; letter-spacing: 2px; }
  .header h1 { font-size: 24px; margin: 0 0 8px; }
  .header .meta { font-size: 14px; color: #666; }
  table { width: 100%; border-collapse: collapse; font-size: 15px; }
  th { background: #f0f2f5; font-weight: bold; padding: 10px 12px; border: 1px solid #d9d9d9; }
  td { padding: 10px 12px; border: 1px solid #d9d9d9; }
  tr:nth-child(even) td { background: #fafafa; }
  .footer { text-align: center; margin-top: 30px; font-size: 13px; color: #999; }
  @media print {
    body { padding: 0; }
    @page { margin: 1.5cm; }
  }
</style></head><body>
<div class="header">
  <div class="sysname">多媒体作品评审系统</div>
  <h1>${report.competitionName}</h1>
  <div class="meta">生成时间: ${now} | 状态: ${report.status === 'FINISHED' ? '已结束' : report.status}</div>
</div>
<table>
  <thead><tr>
    <th style="width:50px">名次</th>
    <th style="width:80px">奖项</th>
    <th style="width:100px">姓名</th>
    <th style="width:90px">部门</th>
    <th style="width:70px">最高分</th>
    <th style="width:70px">最低分</th>
    <th style="width:70px">平均分</th>
  </tr></thead>
  <tbody>${rankRows}</tbody>
</table>
<div class="footer">排名表</div>
${'<scr' + 'ipt>window.onload=function(){window.print();}</scr' + 'ipt>'}
</body></html>`;
    w.document.write(html);
    w.document.close();
  };

  return (
    <div style={{ padding: 24, maxWidth: 1000, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')}>返回</Button>
        <h2 style={{ margin: 0 }}>{report.competitionName} - 排名报表</h2>
        <Tag color="success">{report.status === 'FINISHED' ? '已结束' : report.status}</Tag>
        <Button type="primary" icon={<PrinterOutlined />} onClick={openPrintView}>打印排名表</Button>
      </Space>

      {report.status !== 'FINISHED' && (
        <Card style={{ marginBottom: 16, background: '#fff7e6' }}>
          比赛尚未结束，当前排名为预览结果，最终排名将在比赛结束后确定。
        </Card>
      )}

      {report.ranks?.map((rank: any) => (
        <Card key={rank.rankNumber} style={{ marginBottom: 16 }}
          title={
            <Space>
              <TrophyOutlined style={{ color: rankColors[rank.rankNumber - 1] || '#666' }} />
              <span style={{ fontSize: 18, fontWeight: 'bold', color: rankColors[rank.rankNumber - 1] }}>
                {rank.rankLabel}
              </span>
              <Tag>{rank.participants?.length || 0}人</Tag>
            </Space>
          }>
          {rank.participants?.map((p: any) => (
            <Collapse key={p.participantId} style={{ marginBottom: 8 }}
              items={[{
                key: 'detail',
                label: (
                  <Space>
                    <strong>{p.participantName}</strong>
                    {p.department && <Tag>{p.department}</Tag>}
                    <Tag color="blue">平均分: {p.averageScore}</Tag>
                  </Space>
                ),
                children: (
                  <div>
                    <p><strong>计算过程：</strong>{p.calculationProcess}</p>
                    {p.removedHighest && (
                      <p>去掉最高分: <Tag color="red">{p.removedHighest}</Tag> | 去掉最低分: <Tag color="blue">{p.removedLowest}</Tag></p>
                    )}
                    <p>有效分: {p.effectiveScores?.join(', ')}</p>
                    <Table size="small" pagination={false} dataSource={p.judgeScores}
                      columns={[
                        { title: '评委', dataIndex: 'judgeName', key: 'judgeName' },
                        { title: '评分', dataIndex: 'score', key: 'score' },
                        {
                          title: '标记', key: 'mark',
                          render: (_: any, record: any) => (
                            <Space>
                              {record.isHighest && <Tag color="red">最高分</Tag>}
                              {record.isLowest && <Tag color="blue">最低分</Tag>}
                            </Space>
                          )
                        }
                      ]}
                      rowKey="judgeId" />
                  </div>
                )
              }]} />
          ))}
          {(!rank.participants || rank.participants.length === 0) && <p style={{ color: '#999' }}>暂无人获得此名次</p>}
        </Card>
      ))}

      {(!report.ranks || report.ranks.length === 0) && (
        <Card><p style={{ color: '#999', textAlign: 'center' }}>暂无排名数据</p></Card>
      )}
    </div>
  );
}
