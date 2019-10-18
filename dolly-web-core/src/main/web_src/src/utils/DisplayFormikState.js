import React from 'react'

export default function DisplayFormikState(props) {
	return (
		<pre
			style={{
				position: 'absolute',
				top: 0,
				right: 0,
				background: '#f6f8fa',
				fontSize: '.7rem',
				margin: 0,
				padding: '.8rem',
				borderTop: '1px solid',
				borderLeft: '1px solid',
				borderColor: '#ccc'
			}}
		>
			<strong>props</strong> = {JSON.stringify(props, null, 2)}
		</pre>
	)
}
