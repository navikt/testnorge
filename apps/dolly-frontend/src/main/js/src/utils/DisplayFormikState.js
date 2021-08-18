import React from 'react'
import { useToggle } from 'react-use'

const FormikState = props => (
	<pre>
		<strong>props</strong> = {JSON.stringify(props, null, 2)}
	</pre>
)

export default function DisplayFormikState({ visState = false, ...props }) {
	const [showState, toggleShowState] = useToggle(visState)

	return (
		<div
			onClick={toggleShowState}
			style={{
				position: 'absolute',
				top: 0,
				right: 0,
				background: '#f6f8fa',
				fontSize: '.7rem',
				margin: 0,
				padding: '.8rem',
				borderBottom: '1px solid',
				borderLeft: '1px solid',
				borderColor: '#ccc',
				zIndex: 99
			}}
		>
			{showState && <FormikState {...props} />}
			{!showState && <span>Vis state</span>}
		</div>
	)
}
