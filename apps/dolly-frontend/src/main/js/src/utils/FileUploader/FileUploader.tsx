import React, { useCallback } from 'react'
import { useDropzone } from 'react-dropzone'
import styled from 'styled-components'

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
	margin-bottom: 10px;
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
export const FileUploader = ({ files, setFiles }: { files: File[]; setFiles: any }) => {
	const handleDrop = useCallback((acceptedFiles: File[]) => {
		const reader = new FileReader()

		acceptedFiles.forEach((file: File) => {
			reader.onabort = () => console.warn('file reading was aborted')
			reader.onerror = () => console.error('file reading has failed')
			reader.onload = () => {
				const binaryStr = reader.result?.slice(28)
				setFiles([
					// @ts-ignore
					{
						id: new Date().getTime(),
						name: file.path,
						content: { base64: binaryStr },
					},
					...files,
				])
			}
			reader.readAsDataURL(file)
		})
	}, [])
	const { getRootProps, getInputProps, isFocused, isDragAccept, isDragReject } = useDropzone({
		multiple: true,
		onDrop: handleDrop,
	})

	return (
		<div className="container">
			<Container {...getRootProps({ isFocused, isDragAccept, isDragReject })}>
				<input {...getInputProps()} />
				<p>Dra og slipp filer innenfor rammen eller klikk her for å åpne filvelger</p>
			</Container>
		</div>
	)
}
