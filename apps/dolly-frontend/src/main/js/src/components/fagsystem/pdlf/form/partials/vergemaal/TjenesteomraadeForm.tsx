import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'

const initialTjenesteOppgaveOptions = [
	{ label: 'Hjelpemidler', value: 'hjelpemidler' },
	{ label: 'Pensjon', value: 'pensjon' },
	{ label: 'Sosiale tjenester', value: 'sosialeTjenester' },
	{ label: 'Arbeid', value: 'arbeid' },
	{ label: 'Familie', value: 'familie' },
]

export const initialTjenesteomraade = {
	tjenesteoppgave: ['hjelpemidler'],
	tjenestevirksomhet: 'nav',
}

interface TjenesteomraadeFormProps {
	path: string
}

export const TjenesteomraadeForm = ({ path }: TjenesteomraadeFormProps) => {
	return (
		<>
			<FormTextInput name={`${path}.tjenestevirksomhet`} label="Tjenestevirksomhet" />
			<FormSelect
				isClearable={false}
				isMulti
				size={'grow'}
				name={`${path}.tjenesteoppgave`}
				label="Tjenesteoppgaver"
				options={initialTjenesteOppgaveOptions}
			/>
		</>
	)
}
