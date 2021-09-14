import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Vedlegg } from '~/components/fagsystem/dokarkiv/form/scanning/DokarkivForm'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Document, Page } from 'react-pdf/dist/umd/entry.webpack'
import styled from 'styled-components'

type Data = {
	filer: Vedlegg[]
	handleChange: Function
}

const PdfDocument = styled(Document)`
	align-self: center;
	text-align: -webkit-center;
	max-height: 90px;
`
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
							dokNavn: filnavn,
					  }
					: fil
			)
		)
	}

	return (
		<ErrorBoundary>
			<DollyFieldArray data={filer} header={`Dokumentinfo`}>
				{(fil: Vedlegg, index: number) => (
					<div className="flexbox--space">
						<PdfDocument
							className={'dolly-form-input'}
							file={'data:application/pdf;base64,' + fil.content.base64}
						>
							<Page pageNumber={1} height={80} width={60} />
						</PdfDocument>
						<DollyTextInput
							name={null}
							// @ts-ignore
							defaultValue={
								fil.dokNavn ? fil.dokNavn.replace('.pdf', '') : fil.name.replace('.pdf', '')
							}
							onBlur={(event: { target: { value: string } }) =>
								handleBlur(index, event.target.value)
							}
							label={`Tittel på dokument #${index + 1}`}
						/>
					</div>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}
