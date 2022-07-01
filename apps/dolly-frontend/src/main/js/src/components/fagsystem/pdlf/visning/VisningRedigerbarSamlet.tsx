import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import Loading from '~/components/ui/loading/Loading'
import { Formik, FormikProps } from 'formik'
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
import {
	Telefonnummer,
	TelefonnummerForm,
	TelefonnummerFormRedigering,
} from '~/components/fagsystem/pdlf/form/partials/telefonnummer/Telefonnummer'
import { isArray } from 'lodash'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TelefonData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { TelefonnummerLes } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'

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
	Foedsel = 'foedsel',
	Doedsfall = 'doedsfall',
	Statsborgerskap = 'statsborgerskap',
	Innvandring = 'innflytting',
	Utvandring = 'utflytting',
	Boadresse = 'bostedsadresse',
	Oppholdsadresse = 'oppholdsadresse',
	Kontaktadresse = 'kontaktadresse',
	Adressebeskyttelse = 'adressebeskyttelse',
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
}: VisningTypes) => {
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	// let slettet = []

	// console.log('dataVisning: ', dataVisning) //TODO - SLETT MEG
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
		const submit = async () => {
			const id = null
			const itemData = _get(data, path)
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
								// if (mountedRef.current) {
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
		return submit()
	}, [])

	const handleDelete = useCallback((idx) => {
		const slett = async () => {
			const id = _get(initialValues, `${path}[${idx}].id`)
			console.log('idx: ', idx) //TODO - SLETT MEG
			console.log('id: ', id) //TODO - SLETT MEG
			console.log('initialValues: ', initialValues) //TODO - SLETT MEG
			// console.log('path: ', path) //TODO - SLETT MEG
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
								// if (mountedRef.current) {
								// slettet.push(idx)
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

	const getForm = (formikBag: FormikProps<{}>) => {
		switch (path) {
			case Attributt.Telefonnummer:
				return <Telefonnummer formikBag={formikBag} path={path} />
		}
	}

	const validationSchema = Yup.object().shape(
		{
			telefonnummer: ifPresent('telefonnummer', Yup.array().of(telefonnummer)),
		},
		[['telefonnummer', 'telefonnummer']]
	)
	// console.log('initialValues: ', initialValues) //TODO - SLETT MEG
	// console.log('redigertAttributt: ', redigertAttributt) //TODO - SLETT MEG

	const _validate = (values: any) => validate(values, validationSchema)
	const test = _get(initialValues, path)
	return (
		<>
			{visningModus === Modus.LoadingPdlf && <Loading label="Oppdaterer PDL-forvalter..." />}
			{visningModus === Modus.LoadingPdl && <Loading label="Oppdaterer PDL..." />}
			{/*{visningModus === Modus.Les || visningModus === Modus.LoadingPdlfSlett || visningModus === Modus.LoadingPdl && (*/}
			{[Modus.Les, Modus.LoadingPdlfSlett, Modus.LoadingPdlSlett].includes(visningModus) && (
				<DollyFieldArray data={test} header="" nested>
					{(item: TelefonData, idx: number) => {
						const redigertItem = _get(redigertAttributt, `${path}.[${idx}]`)
						const slettetItem = redigertAttributt && !redigertItem
						// console.log('redigertItem: ', redigertItem) //TODO - SLETT MEG
						// console.log('slettet: ', slettet) //TODO - SLETT MEG
						console.log('xxx item: ', item) //TODO - SLETT MEG
						console.log('xxx idx: ', idx) //TODO - SLETT MEG
						return (
							<React.Fragment key={idx}>
								{visningModus === Modus.LoadingPdlfSlett && (
									<Loading label="Oppdaterer PDL-forvalter..." />
								)}
								{visningModus === Modus.LoadingPdlSlett && <Loading label="Oppdaterer PDL..." />}
								{visningModus === Modus.Les && (
									<>
										{/*{slettet.includes(idx) ? (*/}
										{slettetItem ? (
											<pre style={{ margin: '0' }}>Opplysning slettet</pre>
										) : (
											<TelefonnummerLes telefonnummerData={redigertItem || item} idx={idx} />
										)}
										{!slettetItem && (
											<EditDeleteKnapper>
												<Button
													kind="edit"
													onClick={() => setVisningModus(Modus.Skriv)}
													title="Endre"
												/>
												<Button kind="trashcan" onClick={() => openModal()} title="Slett" />
												<DollyModal
													isOpen={modalIsOpen}
													closeModal={closeModal}
													width="40%"
													overflow="auto"
													key={idx}
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
															>
																Ja, jeg er sikker
															</NavButton>
														</div>
													</div>
												</DollyModal>
											</EditDeleteKnapper>
										)}
										<div className="flexbox--full-width">
											{errorMessagePdlf && <div className="error-message">{errorMessagePdlf}</div>}
											{errorMessagePdl && <div className="error-message">{errorMessagePdl}</div>}
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
					initialValues={redigertAttributt ? redigertAttributt : initialValues}
					// initialValues={initialValues}
					onSubmit={handleSubmit}
					enableReinitialize
					// validationSchema={Yup.array().of(telefonnummer)}
					// validationSchema={validationSchema}
					validate={_validate}
				>
					{(formikBag) => {
						console.log('initialValues: ', initialValues) //TODO - SLETT MEG
						console.log('formikBag: ', formikBag.values) //TODO - SLETT MEG
						return (
							<>
								<DollyFieldArray data={test} header="" nested>
									{(item: TelefonData, idx: number) => (
										// <div className="flexbox--flex-wrap">
										<TelefonnummerFormRedigering path={`telefonnummer[${idx}]`} />
										// <TelefonnummerFormRedigering path={idx} />
										// </div>
									)}
								</DollyFieldArray>
								<FieldArrayEdit>
									{/*<div className="flexbox--flex-wrap">{getForm(formikBag)}</div>*/}
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
