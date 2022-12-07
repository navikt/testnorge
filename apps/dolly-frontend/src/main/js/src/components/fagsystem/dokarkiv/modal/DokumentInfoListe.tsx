import React, { BaseSyntheticEvent } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Vedlegg } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Document, Page } from 'react-pdf'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'

type Data = {
	filer: Vedlegg[]
	handleChange: Function
}

const StyledSlettKnapp = styled(Button)`
	align-self: center;
	height: fit-content;
`

const PdfDocument = styled(Document)`
	align-self: center;
	text-align: -webkit-center;
	max-height: 90px;
`
export const DokumentInfoListe = ({ filer, handleChange }: Data) => {
	if (!filer || filer.length < 1) {
		return null
	}

	const handleBlur = (aktivIndex: number, filnavn: string) => {
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

	const handleDeleteByIndex = (deleteIndex: number) =>
		handleChange(filer.filter((fil, index) => index !== deleteIndex))

	return (
		<ErrorBoundary>
			<DollyFieldArray data={filer} header={`Dokumentinfo`}>
				{(fil: Vedlegg, index: number) => (
					<div className="flexbox--space" key={fil.id + '-' + fil.dokNavn + '-' + fil.name}>
						<PdfDocument file={'data:application/pdf;base64,' + fil.content.base64}>
							<Page pageNumber={1} height={80} width={60} />
						</PdfDocument>
						<DollyTextInput
							name={undefined}
							// @ts-ignore
							defaultValue={
								fil.dokNavn ? fil.dokNavn.replace('.pdf', '') : fil.name?.replace('.pdf', '')
							}
							onBlur={(event: BaseSyntheticEvent) => handleBlur(index, event.target.value)}
							label={`Tittel på dokument #${index + 1}`}
						/>
						<StyledSlettKnapp kind="trashcan" onClick={() => handleDeleteByIndex(index)} />
					</div>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}
