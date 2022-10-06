import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import { Formik } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '~/components/ui/button/Button'
import _get from 'lodash/get'
import { DollyApi, PdlforvalterApi, SkjermingApi, TpsMessagingApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import DollyModal from '~/components/ui/modal/DollyModal'
import useBoolean from '~/utils/hooks/useBoolean'
import {
	folkeregisterpersonstatus,
	kjoenn,
	navn,
} from '~/components/fagsystem/pdlf/form/validation/validation'
import { ifPresent } from '~/utils/YupValidations'
import { PersondetaljerSamlet } from '~/components/fagsystem/pdlf/form/partials/persondetaljerSamlet/PersondetaljerSamlet'
import { Checkbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { isEqual } from 'lodash'
import { RedigerLoading, Modus } from '~/components/fagsystem/pdlf/visning/RedigerLoading'

type VisningTypes = {
	getPdlForvalter: Function
	getSkjermingsregister: Function
	dataVisning: any
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
	tpsMessagingData?: any
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

const PersondetaljerVisning = styled.div`
	width: 100%;
	position: relative;
`

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 0;
	top: 0;
	margin: -5px 10px 0 0;
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

const SlettCheckbox = styled(Checkbox)`
	margin-right: 20px;
`

export const VisningRedigerbarPersondetaljer = ({
	getPdlForvalter,
	getSkjermingsregister,
	dataVisning,
	initialValues,
	redigertAttributt = null,
	ident,
	tpsMessagingData,
}: VisningTypes) => {
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [errorMessageSkjerming, setErrorMessageSkjerming] = useState(null)
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

	const skjermingError = (error: any) => {
		error &&
			setErrorMessageSkjerming(
				`Feil ved sletting i skjermingsregisteret: ${error.message || error.toString()}`
			)
		setVisningModus(Modus.Les)
	}

	const mountedRef = useRef(true)

	const handleSubmit = useCallback((data: any) => {
		let harFeil = false
		const submit = async () => {
			setVisningModus(Modus.LoadingPdlf)
			const editFn = async () => {
				for (const attr of Object.keys(data)) {
					const initialData = redigertAttributt
						? _get(redigertAttributt, `${attr}[0]`)
						: _get(initialValues, `${attr}[0]`)
					const itemData = _get(data, `${attr}[0]`)

					if (!isEqual(itemData, initialData)) {
						await PdlforvalterApi.putAttributt(ident, attr, itemData?.id || 0, itemData).catch(
							(error) => {
								pdlfError(error)
								harFeil = true
							}
						)
					}
				}
			}
			return editFn()
				.then(() => {
					if (!harFeil) {
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
		return submit()
	}, [])

	const handleDelete = useCallback((slettAttr) => {
		const pdlf = {
			feil: false,
			oppdatert: false,
		}
		const skjerming = {
			feil: false,
			oppdatert: false,
		}

		const slett = async () => {
			setVisningModus(Modus.Loading)
			const deleteFn = async () => {
				for (const attr of Object.keys(slettAttr)) {
					if (slettAttr[attr]) {
						if (attr === 'skjerming') {
							skjerming.oppdatert = true
							setVisningModus(Modus.LoadingSkjerming)
							await SkjermingApi.deleteSkjerming(ident)
								.catch((error) => {
									skjermingError(error)
									skjerming.feil = true
								})
								.then(() => {
									if (!skjerming.feil) {
										TpsMessagingApi.deleteSkjerming(ident).catch((error) => {
											skjermingError(error)
											skjerming.feil = true
										})
									}
								})
						} else {
							pdlf.oppdatert = true
							setVisningModus(Modus.LoadingPdlf)
							const id = _get(initialValues, `${attr}[0].id`)
							await PdlforvalterApi.deleteAttributt(ident, attr, id).catch((error) => {
								pdlfError(error)
								pdlf.feil = true
							})
						}
					}
				}
			}

			return deleteFn()
				.then(() => {
					if (pdlf.oppdatert && !pdlf.feil) {
						setVisningModus(Modus.LoadingPdl)
						DollyApi.sendOrdre(ident).then(() => {
							getPdlForvalter().then(() => {
								if (mountedRef.current && (!skjerming.oppdatert || !skjerming.feil)) {
									setVisningModus(Modus.Les)
								}
							})
						})
					}
				})
				.catch((error) => {
					pdlError(error)
				})
				.then(() => {
					if (skjerming.oppdatert && !skjerming.feil) {
						setVisningModus(Modus.LoadingSkjerming)
						getSkjermingsregister().then(() => {
							if (mountedRef.current) {
								setVisningModus(Modus.Les)
							}
						})
					}
				})
		}
		mountedRef.current = false
		return slett()
	}, [])

	const validationSchema = Yup.object().shape(
		{
			folkeregisterpersonstatus: ifPresent(
				'folkeregisterpersonstatus',
				Yup.array().of(folkeregisterpersonstatus)
			),
			kjoenn: ifPresent('kjoenn', Yup.array().of(kjoenn)),
			navn: ifPresent('navn', Yup.array().of(navn)),
		},
		[
			['folkeregisterpersonstatus', 'folkeregisterpersonstatus'],
			['kjoenn', 'kjoenn'],
			['navn', 'navn'],
		]
	)

	const harNavn = redigertAttributt
		? redigertAttributt?.navn?.[0]?.fornavn ||
		  redigertAttributt?.navn?.[0]?.mellomnavn ||
		  redigertAttributt?.navn?.[0]?.etternavn
		: initialValues?.navn?.[0]?.fornavn ||
		  initialValues?.navn?.[0]?.mellomnavn ||
		  initialValues?.navn?.[0]?.etternavn

	const harKjoenn = redigertAttributt
		? redigertAttributt?.kjoenn?.[0]?.kjoenn
		: initialValues?.kjoenn?.[0]?.kjoenn

	const harPersonstatus = redigertAttributt
		? redigertAttributt?.folkeregisterpersonstatus?.[0]?.status
		: initialValues?.folkeregisterpersonstatus?.[0]?.status

	const harSkjerming = redigertAttributt?.skjermingsregister
		? redigertAttributt?.skjermingsregister?.skjermetFra
		: initialValues?.skjermingsregister?.skjermetFra

	const SlettModal = () => {
		const slettAttr = {
			navn: false,
			kjoenn: false,
			folkeregisterpersonstatus: false,
			skjerming: false,
		}

		return (
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Sletting</h1>
						<h4>Hvilke opplysninger ønsker du å slette?</h4>
						<div className="flexbox--flex-wrap">
							{harNavn && (
								<SlettCheckbox
									id={'navn'}
									size={'xxsmall'}
									onChange={() => {
										slettAttr.navn = !slettAttr.navn
									}}
								>
									Navn
								</SlettCheckbox>
							)}
							{harKjoenn && (
								<SlettCheckbox
									id={'kjoenn'}
									size={'xxsmall'}
									onChange={() => {
										slettAttr.kjoenn = !slettAttr.kjoenn
									}}
								>
									Kjønn
								</SlettCheckbox>
							)}
							{harPersonstatus && (
								<SlettCheckbox
									id={'folkeregisterpersonstatus'}
									size={'xxsmall'}
									onChange={() => {
										slettAttr.folkeregisterpersonstatus = !slettAttr.folkeregisterpersonstatus
									}}
								>
									Personstatus
								</SlettCheckbox>
							)}
							{harSkjerming && (
								<Checkbox
									id={'skjerming'}
									size={'xxsmall'}
									onChange={() => {
										slettAttr.skjerming = !slettAttr.skjerming
									}}
								>
									Skjerming
								</Checkbox>
							)}
						</div>
					</div>
					<div className="slettModal-actions">
						<NavButton onClick={closeModal}>Avbryt</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								return handleDelete(slettAttr)
							}}
							variant={'primary'}
						>
							Slett
						</NavButton>
					</div>
				</div>
			</DollyModal>
		)
	}

	return (
		<>
			<RedigerLoading visningModus={visningModus} />
			{visningModus === Modus.Les && (
				<PersondetaljerVisning>
					{dataVisning}
					<EditDeleteKnapper>
						<Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} title="Endre" />
						{(harNavn || harKjoenn || harPersonstatus || harSkjerming) && (
							<Button kind="trashcan" onClick={() => openModal()} title="Slett" />
						)}
						<SlettModal />
					</EditDeleteKnapper>
					<div className="flexbox--full-width">
						{errorMessagePdlf && <div className="error-message">{errorMessagePdlf}</div>}
						{errorMessagePdl && <div className="error-message">{errorMessagePdl}</div>}
						{errorMessageSkjerming && <div className="error-message">{errorMessageSkjerming}</div>}
					</div>
				</PersondetaljerVisning>
			)}
			{visningModus === Modus.Skriv && (
				<Formik
					initialValues={redigertAttributt ? redigertAttributt : initialValues}
					onSubmit={handleSubmit}
					enableReinitialize
					validationSchema={validationSchema}
				>
					{(formikBag) => {
						return (
							<>
								<FieldArrayEdit>
									<div className="flexbox--flex-wrap">
										<PersondetaljerSamlet
											formikBag={formikBag}
											tpsMessaging={tpsMessagingData}
											harSkjerming={harSkjerming}
										/>
									</div>
									<Knappegruppe>
										<NavButton
											variant={'primary'}
											onClick={() => setVisningModus(Modus.Les)}
											disabled={formikBag.isSubmitting}
											style={{ top: '1.75px' }}
										>
											Avbryt
										</NavButton>
										<NavButton
											variant={'primary'}
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
