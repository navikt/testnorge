import React, { useCallback, useEffect, useRef, useState } from 'react'
import * as Yup from 'yup'
import Button from '~/components/ui/button/Button'
import DollyModal from '~/components/ui/modal/DollyModal'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
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
import Loading from '~/components/ui/loading/Loading'
import { Hjelpetekst } from '~/components/hjelpetekst/Hjelpetekst'

const PersonvelgerCheckboxes = styled.div`
	overflow-y: scroll;
	max-height: 22rem;
	border: 1px solid #ccc;
	border-radius: 4px;
	padding: 10px;
`

const ValgtePersonerList = styled.div`
	overflow-y: scroll;
	max-height: 22rem;
	display: block;
	margin-left: 20px;
`

const ModalContent = styled.div`
	display: flex;
	align-items: left;
	flex-direction: column;
	&&& {
		h2 {
			font-size: 1.2em;
		}
	}
`

const GruppeVelger = styled.div`
	margin-bottom: 20px;
`

const PersonVelger = styled.div`
	display: flex;
	flex-wrap: wrap;
	border-top: 1px solid #ccc;
	padding-top: 10px;
`

const PersonKolonne = styled.div`
	display: flex;
	flex-direction: column;
	width: 50%;
	&&& {
		div {
			margin-right: 0;
		}
	}
`

export const FlyttPersonButton = ({ gruppeId, fagsystem, disabled }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [searchText, setSearchText] = useState('')
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null)

	const [gruppeIdenter, setGruppeidenter] = useState(null)

	const navigate = useNavigate()

	useEffect(() => {
		const getGruppeIdenter = async () => {
			const oppslag = await SelectOptionsOppslag.hentIdentNavnOptions(gruppeId)
				.then((response) => {
					if (!response || response.length === 0) {
						return []
					}
					return response
				})
				.catch((error) => {
					setError(error)
					return []
				})
			setGruppeidenter(oppslag)
		}
		getGruppeIdenter()
	}, [modalIsOpen])

	const gruppeIdenterListe = Array.isArray(gruppeIdenter)
		? gruppeIdenter?.map((person) => person.value)
		: []

	const mountedRef = useRef(true)

	const handleSubmit = useCallback(
		(formikBag) => {
			const submit = async () => {
				setLoading(true)
				const { gruppeId, identer } = formikBag
				const relasjoner = [] as Array<string>
				identer.forEach((ident) => {
					relasjoner.push(
						...gruppeIdenter?.find((gruppeIdent) => gruppeIdent.value === ident)?.relasjoner
					)
				})
				const identerSamlet = Array.from(new Set([...identer, ...relasjoner]))
				await DollyApi.flyttPersonerTilGruppe(gruppeId, identerSamlet)
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
		},
		[gruppeIdenter]
	)

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
			<DollyModal isOpen={modalIsOpen} closeModal={handleClose} minWidth="50%" overflow="auto">
				<ModalContent>
					<Formik
						initialValues={{ identer: [], gruppeId: null }}
						onSubmit={handleSubmit}
						validationSchema={validation}
					>
						{(formikBag) => {
							return (
								<>
									<h1>Flytt personer til gruppe</h1>
									<GruppeVelger>
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
									</GruppeVelger>
									<PersonVelger>
										<FieldArray name="identer">
											{({ push, remove, form }) => {
												const values = form.values?.identer
												const isChecked = (id: string) => values?.includes(id)
												const onClick = (e: { target }) => {
													const { id } = e.target
													isChecked(id) ? remove(values?.indexOf(id)) : push(id)
												}
												return (
													<PersonKolonne>
														<div className="flexbox--align-center">
															<h2>Velg personer</h2>
															<Hjelpetekst>
																Personer vil bli flyttet til valgt gruppe. Dersom valgte personer
																har relaterte personer vil disse også bli flyttet.
															</Hjelpetekst>
														</div>
														<DollyTextInput
															name="search"
															value={searchText}
															onChange={(e) => setSearchText(e.target.value)}
															size="grow"
															placeholder="Søk etter person"
															icon="search"
														/>
														{!gruppeIdenter ? (
															<Loading label="Laster personer..." />
														) : (
															<PersonvelgerCheckboxes>
																{gruppeIdenter?.map((person) => {
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
														)}
														<div className="flexbox--flex-wrap" style={{ marginTop: '10px' }}>
															<Button
																onClick={() =>
																	formikBag.setFieldValue('identer', gruppeIdenterListe)
																}
															>
																VELG ALLE
															</Button>
															<Button onClick={() => formikBag.setFieldValue('identer', [])}>
																NULLSTILL
															</Button>
														</div>
														<ErrorMessageWithFocus
															name="identer"
															className="error-message"
															component="div"
														/>
													</PersonKolonne>
												)
											}}
										</FieldArray>

										<PersonKolonne>
											<ValgtePersonerList>
												<h2>Valgte personer</h2>
												{_get(formikBag.values, 'identer')?.length > 0 ? (
													<ul>
														{_get(formikBag.values, 'identer')?.map((ident) => (
															<li key={ident}>{ident}</li>
														))}
													</ul>
												) : (
													<span className="utvalg--empty-result">Ingenting er valgt</span>
												)}
											</ValgtePersonerList>
										</PersonKolonne>
									</PersonVelger>

									<div className="flexbox--full-width">
										{error && <div className="error-message">{`Feil: ${error}`}</div>}
									</div>
									<div className="flexbox--justify-center" style={{ marginTop: '15px' }}>
										<NavButton onClick={handleClose}>Avbryt</NavButton>
										<NavButton
											onClick={() => formikBag.handleSubmit()}
											type="hoved"
											disabled={loading}
											spinner={loading}
											style={{ marginLeft: '10px' }}
										>
											Flytt personer
										</NavButton>
									</div>
								</>
							)
						}}
					</Formik>
				</ModalContent>
			</DollyModal>
		</>
	)
}
