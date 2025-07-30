import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';

export function useAlerts() {
  function showAlert({ type = 'info', title = '', message = '', duration } = {}) {
    const iconMap = { danger: 'error', error: 'error', success: 'success', info: 'info', warning: 'warning' };
    const icon = iconMap[type] || 'info';
    const autoClose = duration ?? (type === 'success' ? 2500 : undefined);

    return Swal.fire({
      icon,
      title,
      text: message,
      confirmButtonText: 'Ok',
      confirmButtonColor: '#10b981',
      timer: autoClose,
      timerProgressBar: !autoClose,
      customClass: {
        confirmButton: 'swal-custom-btn'
      }
    });
  }

  async function showConfirm({ title, message, confirmText = 'Yes', cancelText = 'Cancel' } = {}) {
    const result = await Swal.fire({
      icon: 'question',
      title,
      text: message,
      showCancelButton: true,
      showConfirmButton: true,
      confirmButtonText: confirmText,
      cancelButtonText: cancelText,
      confirmButtonColor: '#10b981',
      cancelButtonColor: '#6b7280',
      customClass: {
        confirmButton: 'swal-custom-btn'
      }
    });
    return result.isConfirmed;
  }

  async function showPasswordPrompt({ title = 'Enter Password', label = 'Password', confirmText = 'Submit', cancelText = 'Cancel' } = {}) {
    const result = await Swal.fire({
      title,
      input: 'password',
      inputLabel: label,
      inputPlaceholder: 'Enter new password',
      inputAttributes: {
        autocapitalize: 'off',
        autocorrect: 'off'
      },
      showCancelButton: true,
      confirmButtonText: confirmText,
      cancelButtonText: cancelText,
      confirmButtonColor: '#10b981',
      cancelButtonColor: '#6b7280',
      customClass: {
        confirmButton: 'swal-custom-btn'
      }
    });

    return result.isConfirmed ? result.value : null;
  }

  return { showAlert, showConfirm, showPasswordPrompt };
}
