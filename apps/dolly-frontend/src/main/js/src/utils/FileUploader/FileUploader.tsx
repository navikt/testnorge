import React from 'react'
import { useDropzone } from 'react-dropzone'
import styled from 'styled-components'
import * as _ from 'lodash-es'

const getColor = (props: any) => {
	if (props.isDragAccept) {
		return '#06893a'
	}
	if (props.isDragReject) {
		return '#ba3a26'
	}
	if (props.isFocused) {
		return '#0067C5FF'
	}
	return '#eeeeee'
}

const Container = styled.div`
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	width: auto;
	margin-bottom: 10px;
	margin-top: 10px;
	padding: 20px;
	border-width: 2px;
	border-radius: 2px;
	border-color: ${(props) => getColor(props)};
	border-style: dashed;
	background-color: #fafafa;
	color: #bdbdbd;
	outline: none;
	transition: border 0.24s ease-in-out;
`
export default ({
	filer,
	setFiler,
	feil,
	isMultiple = true,
}: {
	filer: File[]
	setFiler: any
	feil?: { feilmelding: string } | null
	isMultiple?: boolean
}) => {
	const handleDrop = (acceptedFiles: File[]) => {
		const reader = new FileReader()

		acceptedFiles.forEach((file: File) => {
			reader.onabort = () => console.warn('file reading was aborted')
			reader.onerror = () => console.error('file reading has failed')
			reader.onload = () => {
				const binaryStr = reader.result?.slice(28)
				isMultiple
					? setFiler([
							// @ts-ignore
							{
								id: new Date().getTime(),
								name: file.path,
								content: { base64: binaryStr },
							},
							...filer,
						])
					: setFiler([
							// @ts-ignore
							{
								id: new Date().getTime(),
								name: file.path,
								content: { base64: binaryStr },
							},
						])
			}
			reader.readAsDataURL(file)
		})
	}

	const { getRootProps, getInputProps, isFocused, isDragAccept, isDragReject } = useDropzone({
		disabled: !isMultiple && filer?.length > 0,
		multiple: isMultiple,
		onDrop: handleDrop,
		accept: {
			'application/pdf': ['.pdf'],
		},
	})

	return (
		<div className="container">
			<Container {...getRootProps({ isFocused, isDragAccept, isDragReject })}>
				<input {...getInputProps()} />
				<p>Dra og slipp filer innenfor rammen eller klikk her for å åpne filvelger</p>
			</Container>
			{feil && !_.isEmpty(feil.feilmelding) && (
				<div role="alert" aria-live="assertive">
					<div className="skjemaelement__feilmelding">{feil.feilmelding}</div>
				</div>
			)}
		</div>
	)
}
