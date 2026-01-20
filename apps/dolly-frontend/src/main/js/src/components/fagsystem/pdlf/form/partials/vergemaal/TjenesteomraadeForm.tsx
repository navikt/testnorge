import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { Option } from '@/service/SelectOptionsOppslag'
import { useState } from 'react'
import { useFormContext } from 'react-hook-form'

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
	const formMethods = useFormContext()
	const [selectedTjenesteoppgaver, setSelectedTjenesteoppgaver] = useState<string[]>(
		formMethods.getValues(`${path}.tjenesteoppgave`) || [],
	)
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
				value={selectedTjenesteoppgaver}
				afterChange={(selectedOptions: Option[]) =>
					setSelectedTjenesteoppgaver(selectedOptions?.map((option) => option.value))
				}
			/>
		</>
	)
}
