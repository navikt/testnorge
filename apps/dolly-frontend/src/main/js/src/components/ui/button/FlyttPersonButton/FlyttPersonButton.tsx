import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import Button from '~/components/ui/button/Button'
import DollyModal from '~/components/ui/modal/DollyModal'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import { DollyApi } from '~/service/Api'
import _get from 'lodash/get'
import { FieldArray, Formik, FormikProps } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import styled from 'styled-components'
import { VelgGruppe } from '~/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import { useNavigate } from 'react-router-dom'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ErrorMessageWithFocus } from '~/utils/ErrorMessageWithFocus'
import Loading from '~/components/ui/loading/Loading'
import { Hjelpetekst } from '~/components/hjelpetekst/Hjelpetekst'
import Icon from '~/components/ui/icon/Icon'
import { Alert } from '@navikt/ds-react'
import { usePdlOptions, useTestnorgeOptions, useTpsOptions } from '~/utils/hooks/useSelectOptions'
import { useGruppeIdenter } from '~/utils/hooks/useGruppe'

type FlyttPersonButtonTypes = {
	gruppeId: number
	disabled: boolean
}

type Person = {
	ident: string
	master: string
}

type Option = {
	value: string
	label: string
	relasjoner: Array<string>
}

type FormikBagTypes = {
	gruppeId: string
	identer: Array<string>
}

const PersonvelgerCheckboxes = styled.div`
	overflow-y: auto;
	max-height: 15rem;
	border: 1px solid #ccc;
	border-radius: 4px;
	padding: 10px;
`

const ValgtePersonerList = styled.div`
	overflow-y: auto;
	max-height: 18.4rem;
	display: block;
	margin-left: 20px;

	&& {
		ul {
			margin-top: 10px;
			line-height: 1.5;
		}
	}
`

const ModalContent = styled.div`
	display: flex;
	flex-direction: column;

	&&& {
		h2 {
			font-size: 1.2em;
		}
	}
`

const GruppeVelger = styled.div`
	margin-bottom: 20px;

	.skjemaelement {
		margin-bottom: 0;
	}

	.error-message {
		margin-top: 10px;
	}

	.navds-button {
		margin-top: 10px;
	}
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

	.navds-alert {
		margin-left: 20px;
		color: #368da8;
	}

	&&& {
		div {
			margin-right: 0;
		}
	}
`

const PersonSoek = styled.div`
	position: relative;

	&& {
		svg {
			position: absolute;
			top: 9px;
			right: 9px;
		}
	}
`

const StyledErrorMessageWithFocus = styled(ErrorMessageWithFocus)`
	margin-top: 10px;
`

export const FlyttPersonButton = ({ gruppeId, disabled }: FlyttPersonButtonTypes) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null)

	const {
		identer: gruppeIdenter,
		loading: gruppeLoading,
		error: gruppeError,
	} = useGruppeIdenter(gruppeId)

	const { data: pdlOptions, loading: pdlLoading, error: pdlError } = usePdlOptions(gruppeIdenter)

	const {
		data: testnorgeOptions,
		loading: testnorgeLoading,
		error: testnorgeError,
	} = useTestnorgeOptions(gruppeIdenter)

	const { data: tpsOptions, loading: tpsLoading, error: tpsError } = useTpsOptions(gruppeIdenter)

	const navigate = useNavigate()

	const getGruppeOptions = () => {
		const options = [] as Array<Option>
		gruppeIdenter?.forEach((person: Person) => {
			if (person.master === 'PDLF' && pdlOptions) {
				options.push(pdlOptions?.find((p: Option) => p.value === person.ident))
			} else if (person.master === 'PDL' && testnorgeOptions) {
				options.push(testnorgeOptions?.find((p: Option) => p.value === person.ident))
			} else if (person.master === 'TPSF' && tpsOptions) {
				options.push(tpsOptions?.find((p: Option) => p.value === person.ident))
			}
		})
		return options
	}

	const gruppeOptions = getGruppeOptions()

	const gruppeIdenterListe = Array.isArray(gruppeOptions)
		? gruppeOptions?.map((person) => person?.value).filter((person) => person)
		: []

	const getRelatertePersoner = (identer: Array<string>, identerHentet = [] as Array<string>) => {
		const relatertePersonerHentet = identerHentet
		const identerNye = [] as Array<string>
		identer.forEach((ident: string) => {
			const funnetIdent = gruppeOptions?.find(
				(gruppeIdent: Option) =>
					gruppeIdent?.value === ident && !relatertePersonerHentet.includes(ident)
			)
			if (funnetIdent) {
				relatertePersonerHentet.push(funnetIdent.value)
				identerNye.push(...funnetIdent.relasjoner)
			}
		})
		if (identerNye.length > 0) {
			getRelatertePersoner(identerNye, relatertePersonerHentet)
		}
		return relatertePersonerHentet
	}

	const harRelatertePersoner = (identer: Array<string>) => {
		if (!identer || identer?.length < 1) {
			return false
		}
		let relatert = false
		identer.forEach((ident) => {
			if (
				gruppeOptions?.find((option: Option) => option?.value === ident)?.relasjoner?.length > 0
			) {
				relatert = true
			}
		})
		return relatert
	}

	const mountedRef = useRef(true)

	const handleSubmit = useCallback(
		(formikBag: any) => {
			const submit = async () => {
				setLoading(true)
				const { gruppeId, identer } = formikBag
				const relasjoner = getRelatertePersoner(identer)
				const identerSamlet = Array.from(new Set([...identer, ...relasjoner]))
				await DollyApi.flyttPersonerTilGruppe(gruppeId, identerSamlet)
					.then(() => {
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
		[gruppeOptions]
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

	const FlyttPersonForm = ({ formikBag }: { formikBag: FormikProps<FormikBagTypes> }) => {
		const [searchText, setSearchText] = useState('')

		return (
			<>
				<h1>Flytt personer til gruppe</h1>
				<GruppeVelger>
					<VelgGruppe
						formikBag={formikBag}
						title={'Velg hvilken gruppe du ønsker å flytte personer til'}
						fraGruppe={gruppeId}
					/>
					<StyledErrorMessageWithFocus name="gruppe" className="error-message" component="div" />
				</GruppeVelger>
				<PersonVelger>
					<FieldArray name="identer">
						{({ push, remove, form }) => {
							const values = form.values?.identer
							const isChecked = (id: string) => values?.includes(id)
							const onClick = (e: { target: any }) => {
								const { id } = e.target
								isChecked(id) ? remove(values?.indexOf(id)) : push(id)
							}
							return (
								<PersonKolonne>
									<div className="flexbox--align-center">
										<h2>Velg personer</h2>
										<Hjelpetekst>
											Personer vil bli flyttet til valgt gruppe. Dersom valgte personer har
											relaterte personer vil disse også bli flyttet.
										</Hjelpetekst>
									</div>
									<PersonSoek>
										<DollyTextInput
											name="search"
											value={searchText}
											onChange={(e: any) => setSearchText(e.target.value)}
											size="grow"
											placeholder="Søk etter person"
										/>
										<Icon kind="search" size={20} />
									</PersonSoek>
									{/*//TODO Sjekk errors! Og sjekk loading istedenfor*/}
									{!gruppeOptions || gruppeOptions?.length < 1 ? (
										<Loading label="Laster personer..." />
									) : (
										<PersonvelgerCheckboxes>
											{gruppeOptions?.map((person: Option) => {
												if (person?.label?.toUpperCase().includes(searchText?.toUpperCase())) {
													return (
														<div key={person.value}>
															<DollyCheckbox
																key={person.value}
																id={person.value}
																label={person.label}
																checked={values?.includes(person.value)}
																onChange={onClick}
																size="small"
																attributtCheckbox
															/>
														</div>
													)
												}
											})}
										</PersonvelgerCheckboxes>
									)}
									<div className="flexbox--flex-wrap" style={{ marginTop: '10px' }}>
										<Button onClick={() => formikBag.setFieldValue('identer', gruppeIdenterListe)}>
											VELG ALLE
										</Button>
										<Button onClick={() => formikBag.setFieldValue('identer', [])}>
											NULLSTILL
										</Button>
									</div>
									<StyledErrorMessageWithFocus
										name="identer"
										className="error-message"
										component="div"
									/>
								</PersonKolonne>
							)
						}}
					</FieldArray>

					<PersonKolonne>
						<h2 style={{ marginLeft: '20px' }}>Valgte personer</h2>
						{harRelatertePersoner(_get(formikBag.values, 'identer')) && (
							<Alert variant="info" size="small" inline>
								Du har valgt én eller flere personer som har relaterte personer. Disse vil også
								flyttes.
							</Alert>
						)}
						<ValgtePersonerList>
							{_get(formikBag.values, 'identer')?.length > 0 ? (
								<ul>
									{_get(formikBag.values, 'identer')?.map((ident: string) => (
										<li key={ident}>
											{gruppeOptions?.find((person: Option) => person?.value === ident)?.label}
										</li>
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
					<NavButton onClick={handleClose} variant="secondary">
						Avbryt
					</NavButton>
					<NavButton
						onClick={() => formikBag.handleSubmit()}
						variant="primary"
						disabled={loading}
						loading={loading}
						style={{ marginLeft: '10px' }}
						type="submit"
					>
						Flytt personer
					</NavButton>
				</div>
			</>
		)
	}

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
						{(formikBag) => <FlyttPersonForm formikBag={formikBag} />}
					</Formik>
				</ModalContent>
			</DollyModal>
		</>
	)
}
