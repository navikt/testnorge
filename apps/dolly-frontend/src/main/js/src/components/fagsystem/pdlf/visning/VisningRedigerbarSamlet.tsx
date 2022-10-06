import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import Loading from '~/components/ui/loading/Loading'
import { Formik } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '~/components/ui/button/Button'
import _get from 'lodash/get'
import { DollyApi, PdlforvalterApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import DollyModal from '~/components/ui/modal/DollyModal'
import useBoolean from '~/utils/hooks/useBoolean'
import { ifPresent, validate } from '~/utils/YupValidations'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { telefonnummer } from '~/components/fagsystem/pdlf/form/validation/partials'
import { TelefonnummerFormRedigering } from '~/components/fagsystem/pdlf/form/partials/telefonnummer/Telefonnummer'
import { TelefonnummerLes } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { RedigerLoading, Modus } from '~/components/fagsystem/pdlf/visning/RedigerLoading'

type VisningTypes = {
	getPdlForvalter: Function
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
	alleSlettet: boolean
	disableSlett: Function
}

enum Attributt {
	Telefonnummer = 'telefonnummer',
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
	margin: 0 0 5px 30px;
	align-content: baseline;
`

export const VisningRedigerbarSamlet = ({
	getPdlForvalter,
	initialValues,
	redigertAttributt = null,
	path,
	ident,
	alleSlettet,
	disableSlett,
}: VisningTypes) => {
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [slettId, setSlettId] = useState(null)

	const openDeleteModal = (idx: number) => {
		setSlettId(idx)
		openModal()
	}

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

	const sendOrdre = () => {
		DollyApi.sendOrdre(ident).then(() => {
			getPdlForvalter().then(() => {
				setVisningModus(Modus.Les)
			})
		})
	}

	const mountedRef = useRef(true)

	const handleSubmit = useCallback((data: any) => {
		const submit = async () => {
			const id = null as number
			const itemData = _get(data, path)?.filter((item: any) => item)
			setVisningModus(Modus.LoadingPdlf)
			await PdlforvalterApi.putAttributt(ident, path, id, itemData)
				.catch((error) => {
					pdlfError(error)
				})
				.then((putResponse) => {
					if (putResponse) {
						setVisningModus(Modus.LoadingPdl)
						sendOrdre()
					}
				})
				.catch((error) => {
					pdlError(error)
				})
		}
		mountedRef.current = false
		return submit()
	}, [])

	const handleDelete = useCallback((idx) => {
		const slett = async () => {
			const id = _get(initialValues, `${path}[${idx}].id`)
			setVisningModus(Modus.LoadingPdlfSlett)
			await PdlforvalterApi.deleteAttributt(ident, path, id)
				.catch((error) => {
					pdlfError(error)
				})
				.then((deleteResponse) => {
					if (deleteResponse) {
						setVisningModus(Modus.LoadingPdlSlett)
						sendOrdre()
					}
				})
				.catch((error) => {
					pdlError(error)
				})
		}
		mountedRef.current = false
		return slett()
	}, [])

	const getVisning = (item: any, idx: number) => {
		if (path === Attributt.Telefonnummer) {
			return <TelefonnummerLes telefonnummerData={item} idx={idx} />
		}
		return null
	}

	const getForm = (itemPath: string) => {
		if (path === Attributt.Telefonnummer) {
			return <TelefonnummerFormRedigering path={itemPath} />
		}
		return null
	}

	const validationSchema = Yup.object().shape(
		{
			telefonnummer: ifPresent('telefonnummer', Yup.array().of(telefonnummer)),
		},
		[['telefonnummer', 'telefonnummer']]
	)

	const _validate = (values: any) => validate(values, validationSchema)

	const initialValuesListe = _get(initialValues, path)

	const getRedigertAttributtListe = () => {
		const liste = [] as Array<any>
		initialValuesListe.forEach((item: any) => {
			const found = _get(redigertAttributt, path)?.find(
				(redigertItem: any) => redigertItem.id === item.id
			)
			if (found) {
				liste.push(found)
			} else {
				liste.push(null)
			}
		})
		return { [path]: liste }
	}
	const redigertAttributtListe = redigertAttributt && getRedigertAttributtListe()

	const disableIdx = disableSlett(_get(redigertAttributtListe, path) || initialValuesListe)

	return (
		<>
			<RedigerLoading visningModus={visningModus} />
			{[Modus.Les, Modus.LoadingPdlfSlett, Modus.LoadingPdlSlett].includes(visningModus) && (
				<DollyFieldArray data={initialValuesListe} header="" nested>
					{(item: any, idx: number) => {
						const redigertItem = _get(redigertAttributtListe, `${path}.[${idx}]`)
						const slettetItem = redigertAttributtListe && !redigertItem

						return (
							<React.Fragment key={idx}>
								{visningModus === Modus.LoadingPdlfSlett && slettId === idx && (
									<Loading label={'Oppdaterer PDL-forvalter...'} />
								)}
								{visningModus === Modus.LoadingPdlSlett && slettId === idx && (
									<Loading label={'Oppdaterer PDL...'} />
								)}
								{(visningModus === Modus.Les || slettId !== idx) && (
									<>
										{slettetItem || alleSlettet ? (
											<pre style={{ margin: '0' }}>Opplysning slettet</pre>
										) : (
											getVisning(redigertItem || item, idx)
										)}
										{!slettetItem && !alleSlettet && (
											<EditDeleteKnapper>
												<Button
													kind="edit"
													onClick={() => setVisningModus(Modus.Skriv)}
													title="Endre"
												/>
												<Button
													kind="trashcan"
													onClick={() => openDeleteModal(idx)}
													title="Slett"
													disabled={disableIdx === idx}
												/>

												<DollyModal
													isOpen={modalIsOpen && slettId === idx}
													closeModal={closeModal}
													width="40%"
													overflow="auto"
												>
													<div className="slettModal">
														<div className="slettModal slettModal-content">
															<Icon size={50} kind="report-problem-circle" />
															<h1>Sletting</h1>
															<h4>
																Er du sikker på at du vil slette denne opplysningen fra personen?
															</h4>
														</div>
														<div className="slettModal-actions">
															<NavButton onClick={closeModal}>Nei</NavButton>

															<NavButton
																onClick={() => {
																	closeModal()
																	return handleDelete(idx)
																}}
																variant={'primary'}
															>
																Ja, jeg er sikker
															</NavButton>
														</div>
													</div>
												</DollyModal>
											</EditDeleteKnapper>
										)}
										<div className="flexbox--full-width">
											{errorMessagePdlf && !slettetItem && (
												<div className="error-message">{errorMessagePdlf}</div>
											)}
											{errorMessagePdl && !slettetItem && (
												<div className="error-message">{errorMessagePdl}</div>
											)}
										</div>
									</>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			)}
			{visningModus === Modus.Skriv && (
				<Formik
					initialValues={redigertAttributt ? redigertAttributtListe : initialValues}
					onSubmit={handleSubmit}
					enableReinitialize
					validate={_validate}
				>
					{(formikBag) => {
						return (
							<>
								<DollyFieldArray
									data={_get(redigertAttributtListe, path) || initialValuesListe}
									header=""
									nested
								>
									{(item: any, idx: number) =>
										item ? (
											getForm(`${path}[${idx}]`)
										) : (
											<pre style={{ margin: '0' }}>Opplysning slettet</pre>
										)
									}
								</DollyFieldArray>
								<FieldArrayEdit>
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
