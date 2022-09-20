import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import Button from '~/components/ui/button/Button'
import DollyModal from '~/components/ui/modal/DollyModal'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import _get from 'lodash/get'
import { FieldArray, Formik } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import styled from 'styled-components'
import { VelgGruppe } from '~/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import { useNavigate } from 'react-router-dom'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ErrorMessageWithFocus } from '~/utils/ErrorMessageWithFocus'

const PersonvelgerCheckboxes = styled.div`
	overflow: scroll;
	max-height: 22rem;
`

const ValgtePersonerList = styled.div`
	overflow: scroll;
	max-height: 22rem;
	display: block;
`

export const FlyttPersonButton = ({ gruppeId, fagsystem, disabled }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [searchText, setSearchText] = useState('')
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null)

	const navigate = useNavigate()

	const getGruppeIdenter = () => {
		return useAsync(
			async () => SelectOptionsOppslag.hentIdentNavnOptions(gruppeId),
			[SelectOptionsOppslag.hentIdentNavnOptions]
		)
	}

	const gruppeIdenter = getGruppeIdenter()

	const gruppeIdenterListe = gruppeIdenter?.values
		? gruppeIdenter?.values?.map((person) => person.value)
		: []

	const identListe = getGruppeIdenter().value?.map((person) => {
		return {
			value: person?.value,
			label: person?.label,
		}
	})

	const mountedRef = useRef(true)

	const handleSubmit = useCallback((formikBag) => {
		const submit = async () => {
			const { gruppeId, identer } = formikBag
			setLoading(true)
			await DollyApi.flyttPersonerTilGruppe(gruppeId, identer)
				.then((response) => {
					closeModal()
					setLoading(false)
					navigate(`../gruppe/${gruppeId}`)
				})
				.catch((e: Error) => {
					setError(e.message)
					setLoading(false)
				})
		}
		mountedRef.current = false
		return submit()
	}, [])

	const handleClose = () => {
		closeModal()
		setError(null)
		setLoading(false)
	}

	const validation = Yup.object().shape({
		identer: Yup.array().min(1, 'Velg minst én person').required(),
		gruppeId: Yup.string().required('Velg eksisterende gruppe eller opprett ny gruppe'),
	})

	return (
		<>
			<Button
				onClick={openModal}
				className="svg-icon-blue-line"
				kind="flytt"
				disabled={disabled}
				title={disabled ? 'Kan ikke flytte personer fra en tom gruppe' : null}
			>
				FLYTT PERSONER
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={handleClose} width="40%" overflow="auto">
				<div className="slettModal">
					<Formik
						initialValues={{ identer: [], gruppeId: null }}
						onSubmit={handleSubmit}
						validationSchema={validation}
					>
						{(formikBag) => {
							console.log('formikBag: ', formikBag) //TODO - SLETT MEG
							return (
								<>
									<h1>Flytt personer til gruppe</h1>
									<div>
										<VelgGruppe
											formikBag={formikBag}
											title={'Velg hvilken gruppe du ønsker å flytte personer til'}
											fraGruppe={gruppeId}
										/>
										<ErrorMessageWithFocus
											name="gruppe"
											className="error-message"
											component="div"
										/>
									</div>
									<div className="flexbox--flex-wrap">
										<FieldArray name="identer">
											{({ push, remove, form }) => {
												const values = form.values?.identer
												const isChecked = (id: string) => values?.includes(id)
												const onClick = (e: { target }) => {
													const { id } = e.target
													isChecked(id) ? remove(values?.indexOf(id)) : push(id)
												}
												return (
													<div className={'flexbox--column'}>
														<div className="flexbox--flex-wrap">
															<DollyTextInput
																name={'search'}
																value={searchText}
																onChange={(e) => setSearchText(e.target.value)}
															/>
														</div>
														<PersonvelgerCheckboxes>
															{identListe?.map((person) => {
																if (
																	person.label?.toUpperCase().includes(searchText?.toUpperCase())
																) {
																	return (
																		<div key={person.value}>
																			<DollyCheckbox
																				key={person.value}
																				id={person.value}
																				label={person.label}
																				checked={values?.includes(person.value)}
																				onChange={onClick}
																				size={'grow'}
																				attributtCheckbox
																			/>
																		</div>
																	)
																}
															})}
														</PersonvelgerCheckboxes>
														<div className="flexbox--flex-wrap">
															<Button
																onClick={() =>
																	formikBag.setFieldValue('identer', gruppeIdenterListe)
																}
															>
																Velg alle
															</Button>
															<Button onClick={() => formikBag.setFieldValue('identer', [])}>
																Nullstill
															</Button>
														</div>
													</div>
												)
											}}
										</FieldArray>
										<ErrorMessageWithFocus
											name="identer"
											className="error-message"
											component="div"
										/>

										<ValgtePersonerList>
											<h4>Valgte personer</h4>
											<ul>
												{_get(formikBag.values, 'identer')?.map((ident) => (
													<li key={ident}>{ident}</li>
												))}
											</ul>
										</ValgtePersonerList>
									</div>
									<div className="flexbox--full-width">
										{error && <div className="error-message">{`Feil: ${error}`}</div>}
									</div>
									<div className="relatertPersonImportModal-actions">
										<NavButton onClick={handleClose}>Avbryt</NavButton>
										<NavButton
											onClick={() => formikBag.handleSubmit()}
											type="hoved"
											disabled={loading}
											spinner={loading}
										>
											Flytt personer
										</NavButton>
									</div>
								</>
							)
						}}
					</Formik>
				</div>
			</DollyModal>
		</>
	)
}
