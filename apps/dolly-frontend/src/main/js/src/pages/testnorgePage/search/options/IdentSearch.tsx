import React, { useEffect, useState } from 'react'
import { FormikProps } from 'formik'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Button from '~/components/ui/button/Button'
import _get from 'lodash/get'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import styled from 'styled-components'

interface IdentSearchProps {
	formikBag: FormikProps<{}>
}

const validIdent = (ident: string) => {
	return ident === '' || ident.match(/^\d{11}$/) != null
}

const ListItem = styled.li`
	display: list-item;
	list-style-type: circle;
`

const identPath = 'personinformasjon.ident.ident'
const identerPath = 'personinformasjon.identer'

export const IdentSearch = ({ formikBag }: IdentSearchProps) => {
	const [identer, setIdenter] = useState(_get(formikBag.values, identerPath))
	const [ident, setIdent] = useState(_get(formikBag.values, identPath))
	const [error, setError] = useState(null)

	useEffect(() => {
		formikBag.setFieldValue(identerPath, identer)
	}, [identer])

	useEffect(() => {
		if (error) {
			formikBag.setFieldValue(identPath, '')
		} else {
			formikBag.setFieldValue(identPath, ident)
		}
	}, [ident])

	const addIdent = () => {
		const currentIdenter = [...identer]
		currentIdenter.push(ident)
		setIdenter(currentIdenter)
		setIdent('')
	}

	const removeIdent = (removedIdent: string) => {
		const updatedIdenter = identer.filter((item: string) => item !== removedIdent)
		setIdenter(updatedIdenter)
	}

	const handleChange = (event: React.ChangeEvent<any>) => {
		event.preventDefault()
		setError(null)
		const idnr = event.target.value
		setIdent(event.target.value)
		if (!validIdent(idnr)) setError('Ident må være et tall med 11 siffer')
		if (identer.includes(idnr)) setError('Ident allerede valgt')
	}

	const handleKeyPress = (event: React.KeyboardEvent<any>) => {
		if (event.key === 'Enter') {
			const idnr = event.target?.value
			if (idnr !== '' && validIdent(idnr)) addIdent()
		}
	}

	return (
		<div>
			{identer?.length > 0 && (
				<ErrorBoundary>
					<ul>
						{identer.map((element: string, idx: number) => (
							<ListItem key={idx}>
								<div className="flexbox--align-center" style={{ lineHeight: '1em' }}>
									{element}
									<Button
										onClick={() => removeIdent(element)}
										kind="trashcan"
										style={{ marginLeft: '10px' }}
									/>
								</div>
							</ListItem>
						))}
					</ul>
				</ErrorBoundary>
			)}
			<DollyTextInput
				name={identPath}
				value={ident}
				onChange={handleChange}
				onKeyPress={handleKeyPress}
				feil={
					error && {
						feilmelding: error,
					}
				}
				size="medium"
			/>
			{ident && !ident.isEmpty && !error && (
				<Button onClick={addIdent} kind="add-circle" style={{ marginBottom: '10px' }}>
					Legg til flere
				</Button>
			)}
		</div>
	)
}
