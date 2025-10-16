import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { GtKodeverk } from '@/config/kodeverk'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useFormContext } from 'react-hook-form'
import { useEffect, useState } from 'react'

interface UtenlandskAdresseIFrittFormatValues {
	path: string
}

export const UtenlandskAdresseIFrittFormat = ({ path }: UtenlandskAdresseIFrittFormatValues) => {
	const formMethods = useFormContext()
	const firstLine = formMethods.watch(`${path}.adresselinjer[0]`)
	const [showError, setShowError] = useState(false)

	useEffect(() => {
		if (firstLine && firstLine.trim() !== '') {
			setShowError(false)
		}
	}, [firstLine])

	const handleNewEntry = () => {
		if (!firstLine || firstLine.trim() === '') {
			setShowError(true)
			return
		}
		const current = formMethods.watch(`${path}.adresselinjer`) || []
		formMethods.setValue(`${path}.adresselinjer`, [...current, ''])
		formMethods.trigger(`${path}.adresselinjer`)
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={`${path}.adresselinjer`}
				header="Adresselinje"
				newEntry=""
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				errorText={
					showError ? 'Fyll inn gjeldende adresselinje f\u00f8r du legger til en ny' : undefined
				}
			>
				{(linePath: string, idx: number) => (
					<FormTextInput name={linePath} label={`Adresselinje ${idx + 1}`} />
				)}
			</FormDollyFieldArray>
			<FormTextInput name={`${path}.postkode`} label="Postkode" />
			<FormTextInput name={`${path}.byEllerStedsnavn`} label="By eller sted" />
			<FormSelect
				name={`${path}.landkode`}
				label="Land"
				kodeverk={GtKodeverk.LAND}
				isClearable={false}
				size="large"
			/>
		</div>
	)
}
