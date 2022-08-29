import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'
import { DollyApi } from '~/service/Api'
import './PartnerImportButton.less'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { Formik, FieldArray } from 'formik'

type Props = {
	partnerIdent: string[]
	gruppeId: string
	gruppeIdenter: string[]
	master: string
}

export const PartnerImportButton = ({ gruppeId, partnerIdent, gruppeIdenter, master }: Props) => {
	const [loading, setLoading] = useState(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [feilmelding, setFeilmelding] = useState(null)
	const [fullfoert, setFullfoert] = useState(false)

	if (!partnerIdent) {
		return null
	}

	const disabled = partnerIdent.every((ident) => gruppeIdenter.includes(ident.id))

	const handleImport = async (identer = null as string[]) => {
		setLoading(true)
		setFeilmelding(null)

		await Promise.allSettled(
			identer.map((ident) => {
				DollyApi.importerPartner(gruppeId, ident, master)
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
				title={disabled ? 'Partner er allerede i gruppen' : ''}
				kind="relasjoner"
				className="svg-icon-blue"
			>
				{partnerIdent.length > 1 ? 'IMPORTER RELATERTE PERSONER' : 'IMPORTER RELATERT PERSON'}
			</Button>
			{feilmelding && (
				<div className="error-message" style={{ margin: '5px 0 0 30px' }}>
					{feilmelding}
				</div>
			)}
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="partnerImportModal partnerImportModal-content">
					<Icon size={50} kind="personinformasjon" />
					{partnerIdent.length > 1 ? (
						<>
							<h1>Importer relaterte personer</h1>
							<h4>Velg hvilke relaterte personer du ønsker å importere.</h4>
							{/*TODO: Fix onSubmit*/}
							<Formik initialValues={{ identer: [] }} onSubmit={null}>
								{(formikBag) => (
									<>
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
														{partnerIdent.map((ident) => {
															const disabledCheckbox = gruppeIdenter?.includes(ident.id)
															return (
																<div
																	key={ident.id}
																	title={
																		disabledCheckbox ? 'Relatert person er allerede i gruppen' : ''
																	}
																>
																	<DollyCheckbox
																		key={ident.id}
																		id={ident.id}
																		label={`${ident.type} (${ident.id})`}
																		checked={values?.includes(ident.id)}
																		onChange={onClick}
																		size={'medium'}
																		disabled={disabledCheckbox}
																	/>
																</div>
															)
														})}
													</div>
												)
											}}
										</FieldArray>
										<div className="partnerImportModal-actions">
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
							<h1>Importer relatert person</h1>
							<h4>
								Er du sikker på at du vil importere og legge til valgt persons partner i gruppen?
							</h4>
							<div className="partnerImportModal-actions">
								<NavButton onClick={closeModal}>Nei</NavButton>
								<NavButton
									onClick={() => {
										closeModal()
										handleImport([partnerIdent[0]?.id])
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
