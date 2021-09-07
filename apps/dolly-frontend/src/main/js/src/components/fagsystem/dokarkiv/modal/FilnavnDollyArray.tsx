import React from 'react'
import {ErrorBoundary} from '~/components/ui/appError/ErrorBoundary'
import {DollyTextInput} from '~/components/ui/form/inputs/textInput/TextInput'
import {Vedlegg} from '~/components/fagsystem/dokarkiv/form/scanning/DokarkivForm'
import {DollyFieldArray} from '~/components/ui/form/fieldArray/DollyFieldArray'

type Data = {
	filer: Vedlegg[]
	handleChange: Function
}
export const FilnavnDollyArray = ({ filer, handleChange }: Data) => {
	if (!filer || filer.length < 1) {
		return null
	}

	function handleBlur(aktivIndex: number, filnavn: string) {
		handleChange(
			filer.map((fil, index) =>
				index === aktivIndex
					? {
							...fil,
							dokNavn: filnavn
					  }
					: fil
			)
		)
	}

	return (
		<ErrorBoundary>
			<DollyFieldArray data={filer} header={`Dokumentinfo`}>
				{(fil: Vedlegg, index: number) => (
					<DollyTextInput
						name={null}
						// @ts-ignore
						defaultValue={
							fil.dokNavn ? fil.dokNavn.replace('.pdf', '') : fil.name.replace('.pdf', '')
						}
						onBlur={(event: { target: { value: string } }) => handleBlur(index, event.target.value)}
						label={`Tittel på dokument #${index + 1}`}
					/>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}
