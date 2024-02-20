import { useToggle } from 'react-use'
import { useFormContext } from 'react-hook-form'

const FormState = ({ values }) => (
	<pre>
		<strong>props</strong> = {JSON.stringify(values, replacer, 2)}
	</pre>
)

const replacer = (key: string, value: any) => {
	const exludedProperties = ['fysiskDokument', 'base64']
	if (exludedProperties.some((excludedKey) => excludedKey === key)) {
		return '**Forkortet verdi**'
	} else return value
}

export default function DisplayFormikState({ visState = false, label = 'Vis form' }) {
	const { getValues } = useFormContext()
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
				zIndex: 99,
			}}
		>
			{showState && <>{<FormState values={getValues()} />}</>}
			{!showState && <span style={{ zIndex: 1 }}>{label}</span>}
		</div>
	)
}
