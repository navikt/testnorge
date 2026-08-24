import './LinkButton.less'

export default function LinkButton({ text, onClick, disabled = false, ...rest }) {
	const handleClick = (event) => {
		event.preventDefault()
		onClick(event)
	}

	return (
		<a
			href="#"
			className={disabled ? 'dolly-link-button-disabled' : 'dolly-link-button'}
			onClick={handleClick}
			{...rest}
		>
			{text}
		</a>
	)
}
