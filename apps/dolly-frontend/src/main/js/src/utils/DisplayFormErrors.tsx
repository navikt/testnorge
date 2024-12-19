import { useToggle } from 'react-use'

const FormErrors = ({ errors }) => {
	return (
		<pre>
			<strong>Errors</strong> = {JSON.stringify(errors, replacer, 2)}
		</pre>
	)
}

const replacer = (key: string, value: any) => {
	const exludedProperties = ['ref']
	if (exludedProperties.some((excludedKey) => excludedKey === key)) {
		return '**Fjernet**'
	} else return value
}

const DisplayFormErrors = ({ visState = false, errors = null as any, label = 'Vis state' }) => {
	const [showState, toggleShowState] = useToggle(visState)
	return (
		<div
			onClick={toggleShowState}
			style={{
				position: 'fixed',
				maxHeight: '100%',
				overflow: 'auto',
				top: 40,
				right: 0,
				background: '#f6f8fa',
				fontSize: '.7rem',
				margin: 0,
				padding: '.8rem',
				borderBottom: '1px solid',
				borderLeft: '1px solid',
				borderColor: '#ccc',
				zIndex: 10,
			}}
		>
			{showState && <FormErrors errors={errors} />}
			{!showState && <span>{label}</span>}
		</div>
	)
}

export default DisplayFormErrors
