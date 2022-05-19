import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import Loading from '~/components/ui/loading/Loading'
import { Formik, FormikProps } from 'formik'
import { FoedselForm } from '~/components/fagsystem/pdlf/form/partials/foedsel/Foedsel'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '~/components/ui/button/Button'
import _get from 'lodash/get'
import { DollyApi, PdlforvalterApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import DollyModal from '~/components/ui/modal/DollyModal'
import useBoolean from '~/utils/hooks/useBoolean'
import { StatsborgerskapForm } from '~/components/fagsystem/pdlf/form/partials/statsborgerskap/Statsborgerskap'
import { DoedsfallForm } from '~/components/fagsystem/pdlf/form/partials/doedsfall/Doedsfall'
import {
	doedsfall,
	innflytting,
	statsborgerskap,
	utflytting,
} from '~/components/fagsystem/pdlf/form/validation'
import { ifPresent } from '~/utils/YupValidations'
import { InnvandringForm } from '~/components/fagsystem/pdlf/form/partials/innvandring/Innvandring'
import { UtvandringForm } from '~/components/fagsystem/pdlf/form/partials/utvandring/Utvandring'
import { PersondetaljerSamlet } from '~/components/fagsystem/pdlf/form/partials/persondetaljerSamlet/PersondetaljerSamlet'

type VisningTypes = {
	getPdlForvalter: Function
	dataVisning: any
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
}

enum Modus {
	Les = 'LES',
	Skriv = 'SKRIV',
	LoadingPdlf = 'LOADING_PDLF',
	LoadingPdl = 'LOADING_PDL',
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

export const VisningRedigerbarPersondetaljer = ({
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

	const pdlfError = (error: any) => {
		error &&
			setErrorMessagePdlf(
				`Feil ved oppdatering i PDL-forvalter: ${error.message || error.toString()}`
			)
		setVisningModus(Modus.Les)
	}

	const pdlError = (error: any) => {
		error && setErrorMessagePdl(`Feil ved oppdatering i PDL: ${error.message || error.toString()}`)
		setVisningModus(Modus.Les)
	}

	const mountedRef = useRef(true)

	const handleSubmit = useCallback((data: any) => {
		// console.log('data: ', data) //TODO - SLETT MEG
		// console.log('path: ', path) //TODO - SLETT MEG
		// return Object.keys(data).forEach((attr) => {
		const submit = async () => {
			// const id = _get(data, `${path}.id`)
			setVisningModus(Modus.LoadingPdlf)
			await Promise.allSettled(
				Object.keys(data).map((attr) => {
					console.log('attr: ', attr) //TODO - SLETT MEG
					console.log('data: ', data) //TODO - SLETT MEG
					const itemData = _get(data, `${attr}[0]`)
					PdlforvalterApi.putAttributt(ident, attr, itemData?.id, itemData).catch((error) => {
						pdlfError(error)
					})
				})
			)
				.then((putResponse) => {
					console.log('putResponse: ', putResponse) //TODO - SLETT MEG
					if (putResponse) {
						setVisningModus(Modus.LoadingPdl)
						DollyApi.sendOrdre(ident).then(() => {
							getPdlForvalter().then(() => {
								if (mountedRef.current) {
									setVisningModus(Modus.Les)
								}
							})
						})
					}
				})
				.catch((error) => {
					pdlError(error)
				})

			// await Object.keys(data).forEach((attr) => {
			// 	const itemData = _get(data, `${attr}[0]`)
			// 	PdlforvalterApi.putAttributt(ident, attr, 0, itemData).catch((error) => {
			// 		pdlfError(error)
			// 	})
			// })
			// setVisningModus(Modus.LoadingPdl)
			// DollyApi.sendOrdre(ident)
			// 	.then(() => {
			// 		getPdlForvalter().then(() => {
			// 			if (mountedRef.current) {
			// 				setVisningModus(Modus.Les)
			// 			}
			// 		})
			// 	})
			// 	.catch((error) => {
			// 		pdlError(error)
			// 	})
		}
		mountedRef.current = false
		return submit()
		// })
	}, [])

	const handleDelete = useCallback(() => {
		const slett = async () => {
			const id = _get(initialValues, `${path}.id`)
			setVisningModus(Modus.LoadingPdlf)
			await PdlforvalterApi.deleteAttributt(ident, path, id)
				.catch((error) => {
					pdlfError(error)
				})
				.then((deleteResponse) => {
					if (deleteResponse) {
						setVisningModus(Modus.LoadingPdl)
						DollyApi.sendOrdre(ident).then(() => {
							getPdlForvalter().then(() => {
								if (mountedRef.current) {
									setVisningModus(Modus.Les)
								}
							})
						})
					}
				})
				.catch((error) => {
					pdlError(error)
				})
		}
		mountedRef.current = false
		return slett()
	}, [])

	const validationSchema = Yup.object().shape(
		{
			doedsfall: ifPresent('doedsfall', doedsfall),
			statsborgerskap: ifPresent('statsborgerskap', statsborgerskap),
			innflytting: ifPresent('innflytting', innflytting),
			utflytting: ifPresent('utflytting', utflytting),
		},
		[
			['doedsfall', 'doedsfall'],
			['statsborgerskap', 'statsborgerskap'],
			['innflytting', 'innflytting'],
			['utflytting', 'utflytting'],
		]
	)

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
									<NavButton onClick={closeModal}>Nei</NavButton>
									<NavButton
										onClick={() => {
											closeModal()
											return handleDelete()
										}}
										type="hoved"
									>
										Ja, jeg er sikker
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
					validationSchema={validationSchema}
				>
					{(formikBag) => {
						// console.log('formikBag.values: ', formikBag.values) //TODO - SLETT MEG
						// console.log('formikBag.errors: ', formikBag.errors) //TODO - SLETT MEG
						return (
							<>
								<FieldArrayEdit>
									<div className="flexbox--flex-wrap">
										<PersondetaljerSamlet path={path} formikBag={formikBag} />
									</div>
									<Knappegruppe>
										<NavButton
											type="standard"
											htmlType="reset"
											onClick={() => setVisningModus(Modus.Les)}
											disabled={formikBag.isSubmitting}
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
