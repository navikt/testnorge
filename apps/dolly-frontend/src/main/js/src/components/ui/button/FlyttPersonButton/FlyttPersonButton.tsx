import React, { useState } from 'react'
import Button from '~/components/ui/button/Button'
import DollyModal from '~/components/ui/modal/DollyModal'
import Icon from '~/components/ui/icon/Icon'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { REGEX_BACKEND_GRUPPER } from '~/utils/hooks/useMutate'
import useBoolean from '~/utils/hooks/useBoolean'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import { VelgGruppeToggle } from '~/components/velgGruppe/VelgGruppeToggle'
import _get from 'lodash/get'
import { FieldArray, Formik } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import Formatters from '~/utils/DataFormatter'
import styled from 'styled-components'
import { VelgGruppe } from '~/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import DollyService from '~/service/services/dolly/DollyService'
import { useNavigate } from 'react-router-dom'

const PersonvelgerCheckboxes = styled.div`
	overflow: scroll;
	max-height: 22rem;
`

const ValgtePersonerList = styled.div`
	overflow: scroll;
	max-height: 22rem;
	display: block;
`

export const FlyttPersonButton = ({ gruppeId, disabled }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [valgtGruppe, setValgtGruppe] = useState(null)
	const [error, setError] = useState(null)

	const navigate = useNavigate()

	const getGruppeIdenter = () => {
		return useAsync(async () => DollyApi.getGruppeById(gruppeId), [DollyApi.getGruppeById])
	}

	const gruppeIdenter = getGruppeIdenter().value?.data?.identer?.map((person) => {
		return {
			value: person.ident,
			label: `${person.ident} - Navn Navnesen`,
		}
	})
	// console.log('gruppeIdenter: ', gruppeIdenter) //TODO - SLETT MEG
	// console.log('valgtGruppe: ', valgtGruppe) //TODO - SLETT MEG

	const handleSubmit = async (formikBag) => {
		const { gruppeId, identer } = formikBag.values
		await DollyApi.flyttPersonerTilGruppe(gruppeId, identer)
			.then((response) => {
				console.log('response: ', response) //TODO - SLETT MEG
				closeModal()
				navigate(`../gruppe/${gruppeId}`)
			})
			.catch((e: Error) => {
				setError(e.message)
			})
		// handleImport(formikBag.values?.identer)
		// TODO: SJEKK: Plutselig Syndebukk i Fagsysteminfo
	}
	console.log('error: ', error) //TODO - SLETT MEG

	const handleClose = () => {
		closeModal()
		setError(null)
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
			<DollyModal isOpen={modalIsOpen} closeModal={handleClose} width="40%" overflow="auto">
				<div className="slettModal">
					{/*<div className="slettModal slettModal-content">*/}
					{/*<Icon size={50} kind="report-problem-circle" />*/}

					{/*</div>*/}

					<Formik initialValues={{ identer: [], gruppeId: null }} onSubmit={null}>
						{(formikBag) => (
							<>
								<h1>Flytt personer til gruppe</h1>
								<div>
									{/*<h2>Velg hvilken gruppe du ønsker å flytte personer til.</h2>*/}
									<VelgGruppe formikBag={formikBag} />
									{/*<VelgGruppeToggle valgtGruppe={valgtGruppe} setValgtGruppe={setValgtGruppe} />*/}
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
											console.log('formikBag: ', formikBag.values) //TODO - SLETT MEG
											return (
												// <div className="miljo-velger_checkboxes">
												<PersonvelgerCheckboxes>
													{gruppeIdenter.map((person) => {
														// const disabledCheckbox = gruppeIdenter?.includes(ident.id)
														return (
															<div key={person.value}>
																<DollyCheckbox
																	key={person.value}
																	id={person.value}
																	label={person.label}
																	checked={values?.includes(person.value)}
																	onChange={onClick}
																	size={'grow'}
																	// disabled={disabledCheckbox}
																	attributtCheckbox
																/>
															</div>
														)
													})}
												</PersonvelgerCheckboxes>
											)
										}}
									</FieldArray>
									<ValgtePersonerList>
										<h4>Valgte personer</h4>
										<ul>
											{_get(formikBag.values, 'identer').map((ident) => (
												<li key={ident}>{ident}</li>
											))}
										</ul>
									</ValgtePersonerList>
								</div>
								{/*{error && <p>feil</p>}*/}
								<div className="flexbox--full-width">
									{error && <div className="error-message">{`Feil: ${error}`}</div>}
								</div>
								<div className="relatertPersonImportModal-actions">
									<NavButton onClick={handleClose}>Avbryt</NavButton>
									<NavButton
										onClick={() => {
											handleSubmit(formikBag)
										}}
										type="hoved"
									>
										Flytt personer
									</NavButton>
								</div>
							</>
						)}
					</Formik>
					{/*<div className="slettModal-actions">*/}
					{/*	<NavButton onClick={closeModal}>Avbryt</NavButton>*/}
					{/*	<NavButton*/}
					{/*		onClick={() => {*/}
					{/*			closeModal()*/}
					{/*			// gruppeId ? action(gruppeId) : action()*/}
					{/*			// mutate(REGEX_BACKEND_GRUPPER)*/}
					{/*			// navigateHome && navigate('/')*/}
					{/*		}}*/}
					{/*		type="hoved"*/}
					{/*	>*/}
					{/*		Flytt personer*/}
					{/*	</NavButton>*/}
					{/*</div>*/}
				</div>
			</DollyModal>
		</>
	)
}
