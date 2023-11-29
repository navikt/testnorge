import { useToggle } from 'react-use'
import { useFormContext } from 'react-hook-form'

const FormState = ({ values, errors }) => (
	<pre>
		<strong>props</strong> = {JSON.stringify(values, replacer, 2)}
		<p></p>
		<strong>Errors</strong> = {JSON.stringify(errors, replacer, 2)}
	</pre>
)

const replacer = (key: string, value: any) => {
	const exludedProperties = ['fysiskDokument', 'base64', 'ref']
	if (exludedProperties.some((excludedKey) => excludedKey === key)) {
		return '**Forkortet verdi**'
	} else return value
}

export default function DisplayFormikState({ visState = false, ...props }) {
	const {
		getValues,
		formState: { errors },
	} = useFormContext()
	console.log('getValues(): ', getValues()) //TODO - SLETT MEG
	console.log('errors: ', errors) //TODO - SLETT MEG
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
			{showState && <FormState values={getValues()} errors={errors} />}
			{!showState && <span>Vis state</span>}
		</div>
	)
}
