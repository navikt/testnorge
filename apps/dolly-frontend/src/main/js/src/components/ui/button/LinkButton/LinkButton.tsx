import './LinkButton.less'

export default function LinkButton({ text, onClick, ...rest }) {
	const handleClick = (event) => {
		event.preventDefault()
		onClick(event)
	}

	return (
		<a href="#" className="dolly-link-button" onClick={handleClick} {...rest}>
			{text}
		</a>
	)
}
