import React from 'react'

import './LinkButton.less'

export default function LinkButton({ text, onClick }) {
	const handleClick = event => {
		event.preventDefault()
		onClick(event)
	}

	return (
		<a href="#" className="dolly-link-button" onClick={handleClick}>
			{text}
		</a>
	)
}
