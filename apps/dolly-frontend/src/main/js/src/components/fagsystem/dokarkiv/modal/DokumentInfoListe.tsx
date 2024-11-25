import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Vedlegg } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Document, Page, pdfjs } from 'react-pdf'
import 'react-pdf/dist/esm/Page/AnnotationLayer.css'
import 'react-pdf/dist/esm/Page/TextLayer.css'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'

pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`

type Data = {
	filer: Vedlegg[]
	handleChange: Function
	isMultiple?: boolean
}

const StyledSlettKnapp = styled(Button)`
	align-self: center;
	height: fit-content;
`

const PdfDocument = styled(Document)`
	align-self: center;
	text-align: -webkit-center;
	max-height: 90px;
	margin-right: 10px;
`
export default ({ filer, handleChange, isMultiple = true }: Data) => {
	if (!filer || filer.length < 1) {
		return null
	}

	const handleDeleteByIndex = (deleteIndex: number) =>
		handleChange(filer.filter((fil, index) => index !== deleteIndex))

	const firstFile = filer[0]

	return (
		<ErrorBoundary>
			{isMultiple ? (
				<DollyFieldArray data={filer} header={`Dokumentinfo`}>
					{(fil: Vedlegg, index: number) => (
						<div className="flexbox--space" key={fil.id + '-' + fil.name}>
							<PdfDocument
								options={{ isEvalSupported: false }}
								file={'data:application/pdf;base64,' + fil.content.base64}
							>
								<Page pageNumber={1} height={80} width={60} />
							</PdfDocument>
							<DollyTextInput
								name={`histark.vedlegg.${index}.name`}
								label={`Tittel på dokument #${index + 1}`}
							/>
							<StyledSlettKnapp kind="trashcan" onClick={() => handleDeleteByIndex(index)} />
						</div>
					)}
				</DollyFieldArray>
			) : (
				<div className="flexbox" key={firstFile.id + '-' + firstFile.name}>
					<PdfDocument
						options={{ isEvalSupported: false }}
						file={'data:application/pdf;base64,' + firstFile.content.base64}
					>
						<Page pageNumber={1} height={80} width={60} />
					</PdfDocument>
					<DollyTextInput
						name={`histark.vedlegg.0.name`}
						defaultValue={firstFile.name}
						label={`Tittel på dokument`}
					/>
					<StyledSlettKnapp kind="trashcan" onClick={() => handleDeleteByIndex(0)} />
				</div>
			)}
		</ErrorBoundary>
	)
}
