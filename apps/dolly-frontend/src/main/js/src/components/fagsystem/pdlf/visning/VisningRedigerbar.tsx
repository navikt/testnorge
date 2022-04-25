import React, { useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { Formik } from 'formik'
import { FoedselForm } from '~/components/fagsystem/pdlf/form/partials/foedsel/Foedsel'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '~/components/ui/button/Button'
import _get from 'lodash/get'
import { DollyApi, PdlforvalterApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import DollyModal from '~/components/ui/modal/DollyModal'
import useBoolean from '~/utils/hooks/useBoolean'

type VisningTypes = {
	item: any
	idx: number
}

enum Modus {
	Les = 'LES',
	Skriv = 'SKRIV',
	LoadingPdlf = 'LOADING_PDLF',
	LoadingPdl = 'LOADING_PDL',
}

enum Attributt {
	Foedsel = 'foedsel',
}

const FieldArrayEdit = styled.div`
	&&& {
		button {
			position: relative;
			top: 0;
			right: 0;
			margin-right: 10px;
		}
	}
`

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 8px;
	margin-top: -10px;
	&&& {
		button {
			position: relative;
		}
	}
`

const Knappegruppe = styled.div`
	margin: 10px 0 5px 0;
	align-content: baseline;
`

export const VisningRedigerbar = ({
	getPdlForvalter,
	dataVisning,
	initialValues,
	redigertAttributt = null,
	path,
	ident,
}: VisningTypes) => {
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	const handleSubmit = async (data) => {
		setVisningModus(Modus.LoadingPdlf)
		const id = _get(data, `${path}.id`)
		const itemData = _get(data, path)
		await PdlforvalterApi.putAttributt(ident, path, id, itemData)
			.catch((error) => {
				error &&
					setErrorMessagePdlf(
						`Feil ved oppdatering i PDL-forvalter: ${error.message || error.toString()}`
					)
				setVisningModus(Modus.Les)
			})
			.then((putResponse) => {
				if (putResponse) {
					setVisningModus(Modus.LoadingPdl)
					DollyApi.sendOrdre(ident).then(() => {
						getPdlForvalter()
						setVisningModus(Modus.Les)
					})
				}
			})
			.catch((error) => {
				error &&
					setErrorMessagePdl(`Feil ved oppdatering i PDL: ${error.message || error.toString()}`)
				setVisningModus(Modus.Les)
			})
	}

	const handleDelete = async () => {
		const id = _get(initialValues, `${path}.id`)
		setVisningModus(Modus.LoadingPdlf)
		await PdlforvalterApi.deleteAttributt(ident, path, id)
			.catch((error) => {
				error &&
					setErrorMessagePdlf(
						`Feil ved oppdatering i PDL-forvalter: ${error.message || error.toString()}`
					)
				setVisningModus(Modus.Les)
			})
			.then((deleteResponse) => {
				if (deleteResponse) {
					setVisningModus(Modus.LoadingPdl)
					DollyApi.sendOrdre(ident).then(() => {
						getPdlForvalter()
						setVisningModus(Modus.Les)
					})
				}
			})
			.catch((error) => {
				error &&
					setErrorMessagePdl(`Feil ved oppdatering i PDL: ${error.message || error.toString()}`)
				setVisningModus(Modus.Les)
			})
	}

	const getForm = (formikBag) => {
		switch (path) {
			case Attributt.Foedsel:
				return <FoedselForm formikBag={formikBag} path={path} />
		}
	}

	return (
		<>
			{visningModus === Modus.LoadingPdlf && <Loading label="Oppdaterer PDL-forvalter..." />}
			{visningModus === Modus.LoadingPdl && <Loading label="Oppdaterer PDL..." />}
			{visningModus === Modus.Les && (
				<>
					{dataVisning}
					<EditDeleteKnapper>
						<Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} title="Endre" />
						<Button kind="trashcan" onClick={() => openModal()} title="Slett" />
						<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
							<div className="slettModal">
								<div className="slettModal slettModal-content">
									<Icon size={50} kind="report-problem-circle" />
									<h1>Sletting</h1>
									<h4>Er du sikker på at du vil slette denne opplysningen fra personen?</h4>
								</div>
								<div className="slettModal-actions">
									<NavButton onClick={closeModal}>NEI</NavButton>
									<NavButton
										onClick={() => {
											closeModal()
											return handleDelete()
										}}
										type="hoved"
									>
										JA, JEG ER SIKKER
									</NavButton>
								</div>
							</div>
						</DollyModal>
					</EditDeleteKnapper>
					<div className="flexbox--full-width">
						{errorMessagePdlf && <div className="error-message">{errorMessagePdlf}</div>}
						{errorMessagePdl && <div className="error-message">{errorMessagePdl}</div>}
					</div>
				</>
			)}
			{visningModus === Modus.Skriv && (
				<Formik
					initialValues={redigertAttributt ? redigertAttributt : initialValues}
					onSubmit={handleSubmit}
					enableReinitialize
				>
					{(formikBag) => {
						return (
							<>
								<FieldArrayEdit>
									<div className="flexbox--flex-wrap">{getForm(formikBag)}</div>
									<Knappegruppe>
										<NavButton
											type="standard"
											htmlType="reset"
											onClick={() => setVisningModus(Modus.Les)}
											disabled={!formikBag.isValid || formikBag.isSubmitting}
											style={{ top: '1.75px' }}
										>
											Avbryt
										</NavButton>
										<NavButton
											type="hoved"
											htmlType="submit"
											onClick={() => formikBag.handleSubmit()}
											disabled={!formikBag.isValid || formikBag.isSubmitting}
										>
											Endre
										</NavButton>
									</Knappegruppe>
								</FieldArrayEdit>
							</>
						)
					}}
				</Formik>
			)}
		</>
	)
}
