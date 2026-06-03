<template>
  <div class="toolbox-container">
    <div class="page-header">
      <h1 class="page-title">🔐 加密解密工具箱</h1>
      <p class="page-subtitle">MD5 / SHA / AES / Base64 / URL — 纯前端，数据不上传</p>
    </div>

    <a-card class="tool-card">
      <a-tabs v-model:activeKey="activeTab" size="large" type="card">
        <!-- ==================== MD5 ==================== -->
        <a-tab-pane key="md5" tab="MD5">
          <a-space direction="vertical" :size="16" style="width: 100%">
            <div>
              <a-typography-text strong>输入文本</a-typography-text>
              <a-textarea
                v-model:value="md5Input"
                :rows="4"
                placeholder="输入要计算 MD5 的文本..."
                style="margin-top: 8px"
              />
            </div>
            <a-space>
              <a-button type="primary" @click="computeMd5">
                <template #icon><span>🔢</span></template>
                计算 MD5
              </a-button>
              <a-button @click="md5Input = ''; md5Output = ''">清空</a-button>
            </a-space>
            <div v-if="md5Output">
              <a-typography-text strong>MD5 结果（32位小写）</a-typography-text>
              <div class="result-box">
                <code class="result-code">{{ md5Output }}</code>
                <a-button size="small" type="link" @click="copyText(md5Output, 'MD5')">📋 复制</a-button>
              </div>
              <a-typography-text type="secondary" style="font-size: 12px">
                大写：{{ md5Output.toUpperCase() }}
              </a-typography-text>
            </div>
          </a-space>
        </a-tab-pane>

        <!-- ==================== SHA ==================== -->
        <a-tab-pane key="sha" tab="SHA">
          <a-space direction="vertical" :size="16" style="width: 100%">
            <div>
              <a-typography-text strong>SHA 算法</a-typography-text>
              <a-radio-group v-model:value="shaAlgorithm" button-style="solid" style="margin-left: 16px">
                <a-radio-button value="SHA-1">SHA-1</a-radio-button>
                <a-radio-button value="SHA-256">SHA-256</a-radio-button>
                <a-radio-button value="SHA-512">SHA-512</a-radio-button>
              </a-radio-group>
            </div>
            <div>
              <a-typography-text strong>输入文本</a-typography-text>
              <a-textarea
                v-model:value="shaInput"
                :rows="4"
                placeholder="输入要计算哈希的文本..."
                style="margin-top: 8px"
              />
            </div>
            <a-space>
              <a-button type="primary" @click="computeSha">
                <template #icon><span>🔢</span></template>
                计算 {{ shaAlgorithm }}
              </a-button>
              <a-button @click="shaInput = ''; shaOutput = ''">清空</a-button>
            </a-space>
            <div v-if="shaOutput">
              <a-typography-text strong>{{ shaAlgorithm }} 结果</a-typography-text>
              <div class="result-box">
                <code class="result-code sha-result">{{ shaOutput }}</code>
                <a-button size="small" type="link" @click="copyText(shaOutput, shaAlgorithm)">📋 复制</a-button>
              </div>
            </div>
          </a-space>
        </a-tab-pane>

        <!-- ==================== AES ==================== -->
        <a-tab-pane key="aes" tab="AES">
          <a-space direction="vertical" :size="16" style="width: 100%">
            <a-alert type="info" show-icon message="AES 对称加密，使用相同的密钥进行加密和解密" style="margin-bottom: 0" />
            <div>
              <a-typography-text strong>密钥（Secret Key）</a-typography-text>
              <a-input-password
                v-model:value="aesKey"
                placeholder="输入加密/解密密钥（任意长度）"
                style="margin-top: 8px"
              />
              <a-typography-text type="secondary" style="font-size: 11px">⚠️ 请牢记密钥，丢失后无法解密</a-typography-text>
            </div>
            <a-row :gutter="24">
              <a-col :span="12">
                <a-typography-text strong>🔒 加密 — 输入明文</a-typography-text>
                <a-textarea
                  v-model:value="aesPlainInput"
                  :rows="5"
                  placeholder="输入要加密的文本..."
                  style="margin-top: 8px"
                />
                <a-button type="primary" block style="margin-top: 12px" @click="aesEncrypt" :disabled="!aesKey">
                  🔒 加密
                </a-button>
                <div v-if="aesEncryptedOutput" style="margin-top: 12px">
                  <a-typography-text strong>密文（Base64）</a-typography-text>
                  <div class="result-box">
                    <code class="result-code sha-result">{{ aesEncryptedOutput }}</code>
                    <a-button size="small" type="link" @click="copyText(aesEncryptedOutput, 'AES密文')">📋 复制</a-button>
                  </div>
                </div>
              </a-col>
              <a-col :span="12">
                <a-typography-text strong>🔓 解密 — 输入密文</a-typography-text>
                <a-textarea
                  v-model:value="aesCipherInput"
                  :rows="5"
                  placeholder="粘贴 Base64 密文..."
                  style="margin-top: 8px"
                />
                <a-button type="primary" block style="margin-top: 12px" @click="aesDecrypt" :disabled="!aesKey">
                  🔓 解密
                </a-button>
                <div v-if="aesDecryptedOutput" style="margin-top: 12px">
                  <a-typography-text strong>明文</a-typography-text>
                  <div class="result-box">
                    <code>{{ aesDecryptedOutput }}</code>
                    <a-button size="small" type="link" @click="copyText(aesDecryptedOutput, 'AES明文')">📋 复制</a-button>
                  </div>
                </div>
                <div v-if="aesDecryptError" style="margin-top: 12px">
                  <a-alert type="error" :message="aesDecryptError" closable @close="aesDecryptError = ''" />
                </div>
              </a-col>
            </a-row>
          </a-space>
        </a-tab-pane>

        <!-- ==================== Base64 ==================== -->
        <a-tab-pane key="base64" tab="Base64">
          <a-space direction="vertical" :size="16" style="width: 100%">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-typography-text strong>📝 编码 — 输入原文</a-typography-text>
                <a-textarea
                  v-model:value="b64PlainInput"
                  :rows="5"
                  placeholder="输入要 Base64 编码的文本..."
                  style="margin-top: 8px"
                />
                <a-button type="primary" block style="margin-top: 12px" @click="base64Encode">
                  → Base64 编码
                </a-button>
                <div v-if="b64EncodedOutput" style="margin-top: 12px">
                  <a-typography-text strong>编码结果</a-typography-text>
                  <div class="result-box">
                    <code class="result-code sha-result">{{ b64EncodedOutput }}</code>
                    <a-button size="small" type="link" @click="copyText(b64EncodedOutput, 'Base64编码')">📋 复制</a-button>
                  </div>
                </div>
              </a-col>
              <a-col :span="12">
                <a-typography-text strong>🔙 解码 — 输入 Base64</a-typography-text>
                <a-textarea
                  v-model:value="b64EncodedInput"
                  :rows="5"
                  placeholder="粘贴 Base64 编码文本..."
                  style="margin-top: 8px"
                />
                <a-button type="primary" block style="margin-top: 12px" @click="base64Decode">
                  Base64 解码 →
                </a-button>
                <div v-if="b64DecodedOutput" style="margin-top: 12px">
                  <a-typography-text strong>解码结果</a-typography-text>
                  <div class="result-box">
                    <code>{{ b64DecodedOutput }}</code>
                    <a-button size="small" type="link" @click="copyText(b64DecodedOutput, 'Base64解码')">📋 复制</a-button>
                  </div>
                </div>
                <div v-if="b64DecodeError" style="margin-top: 12px">
                  <a-alert type="error" :message="b64DecodeError" closable @close="b64DecodeError = ''" />
                </div>
              </a-col>
            </a-row>
          </a-space>
        </a-tab-pane>

        <!-- ==================== URL ==================== -->
        <a-tab-pane key="url" tab="URL">
          <a-space direction="vertical" :size="16" style="width: 100%">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-typography-text strong>🔗 URL 编码 — 输入原文</a-typography-text>
                <a-textarea
                  v-model:value="urlPlainInput"
                  :rows="5"
                  placeholder="输入要编码的 URL 或文本..."
                  style="margin-top: 8px"
                />
                <a-button type="primary" block style="margin-top: 12px" @click="urlEncode">
                  → URL 编码
                </a-button>
                <div v-if="urlEncodedOutput" style="margin-top: 12px">
                  <a-typography-text strong>编码结果</a-typography-text>
                  <div class="result-box">
                    <code class="result-code sha-result">{{ urlEncodedOutput }}</code>
                    <a-button size="small" type="link" @click="copyText(urlEncodedOutput, 'URL编码')">📋 复制</a-button>
                  </div>
                </div>
              </a-col>
              <a-col :span="12">
                <a-typography-text strong>🔙 URL 解码 — 输入编码文本</a-typography-text>
                <a-textarea
                  v-model:value="urlEncodedInput"
                  :rows="5"
                  placeholder="粘贴 URL 编码文本..."
                  style="margin-top: 8px"
                />
                <a-button type="primary" block style="margin-top: 12px" @click="urlDecode">
                  URL 解码 →
                </a-button>
                <div v-if="urlDecodedOutput" style="margin-top: 12px">
                  <a-typography-text strong>解码结果</a-typography-text>
                  <div class="result-box">
                    <code>{{ urlDecodedOutput }}</code>
                    <a-button size="small" type="link" @click="copyText(urlDecodedOutput, 'URL解码')">📋 复制</a-button>
                  </div>
                </div>
                <div v-if="urlDecodeError" style="margin-top: 12px">
                  <a-alert type="error" :message="urlDecodeError" closable @close="urlDecodeError = ''" />
                </div>
              </a-col>
            </a-row>
          </a-space>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import CryptoJS from 'crypto-js'

// ==================== Tab state ====================
const activeTab = ref('md5')

// ==================== MD5 ====================
const md5Input = ref('')
const md5Output = ref('')

function computeMd5() {
  if (!md5Input.value.trim()) {
    message.warning('请输入文本')
    return
  }
  md5Output.value = CryptoJS.MD5(md5Input.value).toString()
}

// ==================== SHA ====================
const shaInput = ref('')
const shaOutput = ref('')
const shaAlgorithm = ref<'SHA-1' | 'SHA-256' | 'SHA-512'>('SHA-256')

function computeSha() {
  if (!shaInput.value.trim()) {
    message.warning('请输入文本')
    return
  }
  switch (shaAlgorithm.value) {
    case 'SHA-1':
      shaOutput.value = CryptoJS.SHA1(shaInput.value).toString()
      break
    case 'SHA-256':
      shaOutput.value = CryptoJS.SHA256(shaInput.value).toString()
      break
    case 'SHA-512':
      shaOutput.value = CryptoJS.SHA512(shaInput.value).toString()
      break
  }
}

// ==================== AES ====================
const aesKey = ref('')
const aesPlainInput = ref('')
const aesCipherInput = ref('')
const aesEncryptedOutput = ref('')
const aesDecryptedOutput = ref('')
const aesDecryptError = ref('')

function aesEncrypt() {
  if (!aesKey.value) {
    message.warning('请输入密钥')
    return
  }
  if (!aesPlainInput.value.trim()) {
    message.warning('请输入要加密的文本')
    return
  }
  try {
    aesEncryptedOutput.value = CryptoJS.AES.encrypt(aesPlainInput.value, aesKey.value).toString()
  } catch (e: any) {
    message.error('加密失败: ' + (e.message || e))
  }
}

function aesDecrypt() {
  aesDecryptError.value = ''
  aesDecryptedOutput.value = ''
  if (!aesKey.value) {
    message.warning('请输入密钥')
    return
  }
  if (!aesCipherInput.value.trim()) {
    message.warning('请输入密文')
    return
  }
  try {
    const bytes = CryptoJS.AES.decrypt(aesCipherInput.value, aesKey.value)
    const result = bytes.toString(CryptoJS.enc.Utf8)
    if (!result) {
      aesDecryptError.value = '解密失败：密钥不正确或密文已损坏'
      return
    }
    aesDecryptedOutput.value = result
  } catch (e: any) {
    aesDecryptError.value = '解密失败: ' + (e.message || e)
  }
}

// ==================== Base64 ====================
const b64PlainInput = ref('')
const b64EncodedInput = ref('')
const b64EncodedOutput = ref('')
const b64DecodedOutput = ref('')
const b64DecodeError = ref('')

function base64Encode() {
  if (!b64PlainInput.value.trim()) {
    message.warning('请输入要编码的文本')
    return
  }
  b64EncodedOutput.value = CryptoJS.enc.Base64.stringify(
    CryptoJS.enc.Utf8.parse(b64PlainInput.value)
  )
}

function base64Decode() {
  b64DecodeError.value = ''
  b64DecodedOutput.value = ''
  if (!b64EncodedInput.value.trim()) {
    message.warning('请输入 Base64 编码文本')
    return
  }
  try {
    const result = CryptoJS.enc.Base64.parse(b64EncodedInput.value).toString(CryptoJS.enc.Utf8)
    if (!result) {
      b64DecodeError.value = '解码失败：输入不是有效的 Base64 编码'
      return
    }
    b64DecodedOutput.value = result
  } catch (e: any) {
    b64DecodeError.value = '解码失败: ' + (e.message || e)
  }
}

// ==================== URL ====================
const urlPlainInput = ref('')
const urlEncodedInput = ref('')
const urlEncodedOutput = ref('')
const urlDecodedOutput = ref('')
const urlDecodeError = ref('')

function urlEncode() {
  if (!urlPlainInput.value.trim()) {
    message.warning('请输入要编码的文本')
    return
  }
  urlEncodedOutput.value = encodeURIComponent(urlPlainInput.value)
}

function urlDecode() {
  urlDecodeError.value = ''
  urlDecodedOutput.value = ''
  if (!urlEncodedInput.value.trim()) {
    message.warning('请输入 URL 编码文本')
    return
  }
  try {
    urlDecodedOutput.value = decodeURIComponent(urlEncodedInput.value)
  } catch (e: any) {
    urlDecodeError.value = '解码失败: ' + (e.message || e)
  }
}

// ==================== Common ====================
function copyText(text: string, label: string) {
  navigator.clipboard.writeText(text).then(
    () => message.success(`${label} 已复制到剪贴板`),
    () => message.error('复制失败，请手动复制')
  )
}
</script>

<style scoped>
.toolbox-container {
  max-width: 880px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.page-subtitle {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
}

.tool-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04), 0 4px 20px rgba(0, 0, 0, 0.06);
}

.result-box {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: #f0f5ff;
  border: 1px solid #d6e4ff;
  border-radius: 8px;
  padding: 12px 16px;
  margin-top: 8px;
  word-break: break-all;
}

.result-code {
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
  font-size: 13px;
  color: #1a1a2e;
  flex: 1;
  margin-right: 12px;
  line-height: 1.6;
}

.sha-result {
  font-size: 12px;
}
</style>
