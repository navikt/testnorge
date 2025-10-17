import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useFormContext } from 'react-hook-form'
import { ReactNode, useEffect, useState } from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

interface FrittFormatAdresseProps {
	path: string
	extraFields?: (path: string) => ReactNode
	errorMessage?: string
}

export const FrittFormatAdresse = ({
	path,
	extraFields,
	errorMessage,
}: FrittFormatAdresseProps) => {
	const formMethods = useFormContext()
	const [showError, setShowError] = useState(false)
	const message = errorMessage || 'Fyll inn gjeldende adresselinje før du legger til en ny'

	const adresselinjer: string[] = formMethods.watch(`${path}.adresselinjer`) || []
	const lastLineValue =
		adresselinjer.length > 0 ? adresselinjer[adresselinjer.length - 1] || '' : ''

	useEffect(() => {
		if (lastLineValue.trim()) setShowError(false)
	}, [lastLineValue])

	const handleNewEntry = (appendFn: (value: any) => void) => {
		if (!lastLineValue.trim()) {
			setShowError(true)
			return
		}
		appendFn('')
	}

	return (
		<div className="flexbox--flex-wrap">
			<div className="fritt-format-array-wrapper">
				<FormDollyFieldArray
					nested
					name={`${path}.adresselinjer`}
					header="Adresselinje"
					newEntry=""
					canBeEmpty={false}
					handleNewEntry={handleNewEntry}
					errorText={showError ? message : undefined}
				>
					{(linePath: string, idx: number) => (
						<FormTextInput size="xlarge" name={linePath} label={`Adresselinje ${idx + 1}`} />
					)}
				</FormDollyFieldArray>
			</div>
			{extraFields && extraFields(path)}
		</div>
	)
}
