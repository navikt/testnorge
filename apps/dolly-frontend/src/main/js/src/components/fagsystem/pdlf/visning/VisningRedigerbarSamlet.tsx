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
import { telefonnummer } from '~/components/fagsystem/pdlf/form/validation'
import { ifPresent, validate } from '~/utils/YupValidations'
import { TelefonnummerFormRedigering } from '~/components/fagsystem/pdlf/form/partials/telefonnummer/Telefonnummer'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TelefonData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { TelefonnummerLes } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { element } from 'prop-types'

type VisningTypes = {
	getPdlForvalter: Function
	dataVisning: any
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
	identtype?: string
}

enum Modus {
	Les = 'LES',
	Skriv = 'SKRIV',
	LoadingPdlf = 'LOADING_PDLF',
	LoadingPdl = 'LOADING_PDL',
	LoadingPdlfSlett = 'LOADING_PDLF_SLETT',
	LoadingPdlSlett = 'LOADING_PDL_SLETT',
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
	dataVisning,
	initialValues,
	redigertAttributt = null,
	path,
	ident,
	identtype,
	alleSlettet,
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

	const mountedRef = useRef(true)

	const handleSubmit = useCallback((data: any) => {
		console.log('data: ', data) //TODO - SLETT MEG
		const submit = async () => {
			const id = null
			const itemData = _get(data, path)?.filter((item) => item)
			console.log('itemData: ', itemData) //TODO - SLETT MEG
			setVisningModus(Modus.LoadingPdlf)
			await PdlforvalterApi.putAttributt(ident, path, id, itemData)
				.catch((error) => {
					pdlfError(error)
				})
				.then((putResponse) => {
					if (putResponse) {
						setVisningModus(Modus.LoadingPdl)
						DollyApi.sendOrdre(ident).then(() => {
							getPdlForvalter().then(() => {
								setVisningModus(Modus.Les)
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
						DollyApi.sendOrdre(ident).then(() => {
							getPdlForvalter().then(() => {
								// if (_get(initialValues, path).length < 2) {
								// if (_get(initialValues, path).length === 1) {
								// 	if (mountedRef.current) {
								// 		setVisningModus(Modus.Les)
								// 	}
								// } else {
								setVisningModus(Modus.Les)
								// }
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

	// TODO: bruk denne for å lage mer generll komponent
	// const getForm = (formikBag: FormikProps<{}>) => {
	// 	switch (path) {
	// 		case Attributt.Telefonnummer:
	// 			return <Telefonnummer formikBag={formikBag} path={path} />
	// 	}
	// }

	const validationSchema = Yup.object().shape(
		{
			telefonnummer: ifPresent('telefonnummer', Yup.array().of(telefonnummer)),
		},
		[['telefonnummer', 'telefonnummer']]
	)

	const _validate = (values: any) => validate(values, validationSchema)

	//TODO: Hva er denne godt for?
	const test = _get(initialValues, path)

	const getRedigertAttributtListe = () => {
		const liste = []
		test.forEach((item) => {
			const found = _get(redigertAttributt, path)?.find(
				(redigertItem) => redigertItem.id === item.id
			)
			if (found) {
				liste.push(found)
			} else {
				liste.push(null)
			}
		})
		return { telefonnummer: liste }
	}

	const redigertAttributtListe = redigertAttributt && getRedigertAttributtListe()

	// console.log('test: ', test) //TODO - SLETT MEG
	// console.log('redigertAttributtListe: ', redigertAttributtListe) //TODO - SLETT MEG
	return (
		<>
			{visningModus === Modus.LoadingPdlf && <Loading label="Oppdaterer PDL-forvalter..." />}
			{visningModus === Modus.LoadingPdl && <Loading label="Oppdaterer PDL..." />}
			{/*{visningModus === Modus.Les || visningModus === Modus.LoadingPdlfSlett || visningModus === Modus.LoadingPdl && (*/}
			{[Modus.Les, Modus.LoadingPdlfSlett, Modus.LoadingPdlSlett].includes(visningModus) && (
				<DollyFieldArray data={test} header="" nested>
					{(item: TelefonData, idx: number) => {
						// const getRedigertAttributtListe = () => {
						// 	const liste = []
						// 	test.forEach((item) => {
						// 		const found = _get(redigertAttributt, path)?.find(
						// 			(redigertItem) => redigertItem.id === item.id
						// 		)
						// 		if (found) {
						// 			liste.push(found)
						// 		} else {
						// 			liste.push(null)
						// 		}
						// 	})
						// 	return { telefonnummer: liste }
						// }

						const redigertAttributtListe = redigertAttributt && getRedigertAttributtListe()
						// const redigertAttributtListe = test && getRedigertAttributtListe()

						const redigertItem = _get(redigertAttributtListe, `${path}.[${idx}]`)
						const slettetItem = redigertAttributtListe && !redigertItem
						// console.log('initialValues: ', initialValues) //TODO - SLETT MEG
						// console.log('redigertAttributtListe: ', redigertAttributtListe) //TODO - SLETT MEG
						// console.log('redigertAttributt: ', redigertAttributt) //TODO - SLETT MEG
						// console.log('redigertItem: ', redigertItem) //TODO - SLETT MEG
						// console.log('slettetItem: ', slettetItem) //TODO - SLETT MEG

						return (
							<React.Fragment key={idx}>
								{visningModus === Modus.LoadingPdlfSlett && slettId === idx && (
									<Loading label="Oppdaterer PDL-forvalter..." />
								)}
								{visningModus === Modus.LoadingPdlSlett && slettId === idx && (
									<Loading label="Oppdaterer PDL..." />
								)}
								{(visningModus === Modus.Les || slettId !== idx) && (
									<>
										{slettetItem || alleSlettet ? (
											<pre style={{ margin: '0' }}>Opplysning slettet</pre>
										) : (
											<TelefonnummerLes telefonnummerData={redigertItem || item} idx={idx} />
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
													key={`button_${idx}`}
												/>

												<DollyModal
													isOpen={modalIsOpen && slettId === idx}
													closeModal={closeModal}
													width="40%"
													overflow="auto"
													key={`modal_${idx}`}
												>
													<div className="slettModal">
														<div className="slettModal slettModal-content">
															<Icon size={50} kind="report-problem-circle" />
															<h1>Sletting</h1>
															<h4>
																Er du sikker på at du vil slette denne opplysningen fra personen?
															</h4>
														</div>
														<div className="slettModal-actions" key={idx}>
															<NavButton onClick={closeModal}>Nei</NavButton>

															<NavButton
																onClick={() => {
																	closeModal()
																	return handleDelete(idx)
																}}
																type="hoved"
																key={`navbutton_${idx}`}
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
						// console.log(
						// 	'redigertAttributtListe?.telefonnummer: ',
						// 	redigertAttributtListe?.telefonnummer
						// ) //TODO - SLETT MEG
						return (
							<>
								<DollyFieldArray
									data={redigertAttributtListe?.telefonnummer || test}
									header=""
									nested
								>
									{(item: TelefonData, idx: number) =>
										item ? (
											<TelefonnummerFormRedigering
												path={`telefonnummer[${idx}]`}
												formikBag={formikBag}
											/>
										) : (
											<pre style={{ margin: '0' }}>Opplysning slettet</pre>
										)
									}
								</DollyFieldArray>
								<FieldArrayEdit>
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
