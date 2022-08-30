import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'
import { DollyApi } from '~/service/Api'
import './RelatertPersonImportButton.less'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { Formik, FieldArray } from 'formik'
import Formatters from '~/utils/DataFormatter'
import _lowerCase from 'lodash/lowerCase'

type RelatertPersonProps = {
	type: string
	id: string
}

type Props = {
	relatertPersonIdenter: Array<RelatertPersonProps>
	gruppeId: string
	gruppeIdenter: string[]
	master: string
}

export const RelatertPersonImportButton = ({
	gruppeId,
	relatertPersonIdenter,
	gruppeIdenter,
	master,
}: Props) => {
	const [loading, setLoading] = useState(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [feilmelding, setFeilmelding] = useState(null)
	const [fullfoert, setFullfoert] = useState(false)

	if (!relatertPersonIdenter) {
		return null
	}

	const disabled = relatertPersonIdenter.every((ident) => gruppeIdenter.includes(ident.id))

	const foersteRelatertPersonType = _lowerCase(relatertPersonIdenter[0]?.type)

	const handleImport = async (identer = null as string[]) => {
		setLoading(true)
		setFeilmelding(null)

		await Promise.allSettled(
			identer.map((ident) => {
				DollyApi.importerRelatertPerson(gruppeId, ident, master)
					.then((_response) => {
						setLoading(false)
						setFullfoert(true)
					})
					.catch((_error) => {
						setFeilmelding('Noe gikk galt')
						setFullfoert(false)
						setLoading(false)
					})
			})
		)
	}

	if (loading) {
		return <Loading label="importerer..." />
	}

	if (fullfoert) {
		return (
			<div className={'success-text'}>
				<Icon size={16} kind={'feedback-check-circle'} />
				<span>VENNLIGST LUKK VISNING</span>
			</div>
		)
	}

	return (
		<div>
			<Button
				onClick={openModal}
				disabled={disabled}
				title={disabled ? 'Relaterte personer er allerede i gruppen' : ''}
				kind="relasjoner"
				className="svg-icon-blue"
			>
				{relatertPersonIdenter.length > 1
					? 'IMPORTER RELATERTE PERSONER'
					: `IMPORTER ${relatertPersonIdenter[0]?.type}`}
			</Button>
			{feilmelding && (
				<div className="error-message" style={{ margin: '5px 0 0 30px' }}>
					{feilmelding}
				</div>
			)}
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="relatertPersonImportModal">
					<Icon size={50} kind="personinformasjon" />
					{relatertPersonIdenter.length > 1 ? (
						<>
							<h1>Importer relaterte personer</h1>
							<h4>Velg hvilke relaterte personer du ønsker å importere</h4>
							<Formik initialValues={{ identer: [] }} onSubmit={null}>
								{(formikBag) => (
									<>
										<div className="relatertPersonImportModal-content">
											<FieldArray name="identer">
												{({ push, remove, form }) => {
													const values = form.values?.identer
													const isChecked = (id) => values?.includes(id)

													const onClick = (e) => {
														const { id } = e.target
														isChecked(id) ? remove(values?.indexOf(id)) : push(id)
													}
													return (
														<div className="miljo-velger_checkboxes">
															{relatertPersonIdenter.map((ident) => {
																const disabledCheckbox = gruppeIdenter?.includes(ident.id)
																return (
																	<div
																		key={ident.id}
																		title={disabledCheckbox ? 'Person er allerede i gruppen' : ''}
																	>
																		<DollyCheckbox
																			key={ident.id}
																			id={ident.id}
																			label={`${Formatters.allCapsToCapitalized(ident.type)} (${
																				ident.id
																			})`}
																			checked={values?.includes(ident.id)}
																			onChange={onClick}
																			size={'grow'}
																			disabled={disabledCheckbox}
																			attributtCheckbox
																		/>
																	</div>
																)
															})}
														</div>
													)
												}}
											</FieldArray>
										</div>
										<div className="relatertPersonImportModal-actions">
											<NavButton onClick={closeModal}>Avbryt</NavButton>
											<NavButton
												onClick={() => {
													closeModal()
													handleImport(formikBag.values?.identer)
												}}
												type="hoved"
											>
												Importer
											</NavButton>
										</div>
									</>
								)}
							</Formik>
						</>
					) : (
						<>
							<div className="relatertPersonImportModal-content-center">
								<h1>{`Importer ${foersteRelatertPersonType}`}</h1>
								<h4>
									{`Er du sikker på at du vil importere og legge til valgt persons ${
										foersteRelatertPersonType || 'relaterte person'
									} i gruppen?`}
								</h4>
							</div>
							<div className="relatertPersonImportModal-actions">
								<NavButton onClick={closeModal}>Nei</NavButton>
								<NavButton
									onClick={() => {
										closeModal()
										handleImport([relatertPersonIdenter[0]?.id])
									}}
									type="hoved"
								>
									Ja
								</NavButton>
							</div>
						</>
					)}
				</div>
			</DollyModal>
		</div>
	)
}
