import React, { useEffect } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Vedlegg } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Document, Page, pdfjs } from 'react-pdf'
import 'react-pdf/dist/esm/Page/AnnotationLayer.css'
import 'react-pdf/dist/esm/Page/TextLayer.css'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'
import { useFormContext } from 'react-hook-form'

pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`

type Data = {
	filer: Vedlegg[]
	handleChange: Function
	path: string
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

//TODO: Kan slettes
export default ({ filer, handleChange, path, isMultiple = true }: Data) => {
	const formMethods = useFormContext()
	if (!filer || filer.length < 1) {
		return null
	}

	useEffect(() => {
		filer.forEach((fil: Vedlegg, index: number) => {
			if (formMethods.watch(`${path}.${index}.tittel`) === '') {
				formMethods.setValue(`${path}.${index}.tittel`, fil.name)
			}
		})
	}, [filer])

	const handleDeleteByIndex = (deleteIndex: number) =>
		handleChange(filer.filter((fil, index) => index !== deleteIndex))

	const firstFile = filer[0]

	return (
		<ErrorBoundary>
			{isMultiple ? (
				<DollyFieldArray data={filer} header={`Dokumentinfo`}>
					{(fil: Vedlegg, index: number) => (
						<div className="flexbox--space" key={fil.id + '-' + fil.name}>
							<PdfDocument file={'data:application/pdf;base64,' + fil.content.base64}>
								<Page pageNumber={1} height={80} width={60} />
							</PdfDocument>
							<DollyTextInput
								name={`${path}.${index}.tittel`}
								label={`Tittel på dokument #${index + 1}`}
							/>
							<StyledSlettKnapp kind="trashcan" onClick={() => handleDeleteByIndex(index)} />
						</div>
					)}
				</DollyFieldArray>
			) : (
				<div className="flexbox" key={firstFile.id + '-' + firstFile.name}>
					<PdfDocument file={'data:application/pdf;base64,' + firstFile.content.base64}>
						<Page pageNumber={1} height={80} width={60} />
					</PdfDocument>
					<DollyTextInput
						name={`${path}.0.tittel`}
						input={firstFile.name}
						label={`Tittel på dokument`}
					/>
					<StyledSlettKnapp kind="trashcan" onClick={() => handleDeleteByIndex(0)} />
				</div>
			)}
		</ErrorBoundary>
	)
}
