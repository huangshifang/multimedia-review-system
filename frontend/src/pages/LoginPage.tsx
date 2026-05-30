import { useState } from 'react';
import { Form, Input, Button, Card, message, Tabs, Select, Alert } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values: any, isRegister: boolean) => {
    setLoading(true);
    try {
      const url = isRegister ? '/auth/register' : '/auth/login';
      const { data } = await client.post(url, values);
      localStorage.setItem('token', data.token);
      localStorage.setItem('userId', data.userId);
      localStorage.setItem('username', data.username);
      localStorage.setItem('name', data.name);
      localStorage.setItem('role', data.role);
      message.success(isRegister ? '注册成功' : '登录成功');
      navigate('/');
    } catch (err: any) {
      message.error(err.response?.data?.message || '操作失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: '#f0f2f5' }}>
      <Card title="多媒体作品评审系统" style={{ width: 420 }}>
        <Alert
          message="预置账号：管理员 admin/admin123 | 评委 judge1/judge123 | 选手 player1/player123"
          type="info"
          showIcon
          style={{ marginBottom: 16, fontSize: 12 }}
        />
        <Tabs items={[
          {
            key: 'login',
            label: '登录',
            children: (
              <Form onFinish={(v) => onFinish(v, false)}>
                <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
                  <Input prefix={<UserOutlined />} placeholder="用户名" />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="密码" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading} block>登录</Button>
                </Form.Item>
              </Form>
            )
          },
          {
            key: 'register',
            label: '注册',
            children: (
              <Form onFinish={(v) => onFinish(v, true)} initialValues={{ role: 'JUDGE' }}>
                <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
                  <Input prefix={<UserOutlined />} placeholder="用户名" />
                </Form.Item>
                <Form.Item name="name" rules={[{ required: true, message: '请输入姓名' }]}>
                  <Input placeholder="姓名" />
                </Form.Item>
                <Form.Item name="role" label="角色" rules={[{ required: true }]}>
                  <Select options={[
                    { label: '评委', value: 'JUDGE' },
                    { label: '管理员', value: 'ADMIN' },
                    { label: '选手', value: 'PARTICIPANT' }
                  ]} />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true, min: 6, message: '至少6位密码' }]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="密码" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading} block>注册</Button>
                </Form.Item>
              </Form>
            )
          }
        ]} />
      </Card>
    </div>
  );
}
