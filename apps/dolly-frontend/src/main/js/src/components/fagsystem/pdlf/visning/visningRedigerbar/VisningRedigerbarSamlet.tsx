import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import Loading from '@/components/ui/loading/Loading'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'
import * as _ from 'lodash-es'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import Icon from '@/components/ui/icon/Icon'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import useBoolean from '@/utils/hooks/useBoolean'
import { ifPresent } from '@/utils/YupValidations'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TelefonnummerFormRedigering } from '@/components/fagsystem/pdlf/form/partials/telefonnummer/Telefonnummer'
import { TelefonnummerLes } from '@/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import {
	Modus,
	RedigerLoading,
} from '@/components/fagsystem/pdlf/visning/visningRedigerbar/RedigerLoading'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { telefonnummer } from '../../form/validation/partials/telefonnummer'

type VisningTypes = {
	getPdlForvalter: Function
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
	alleSlettet: boolean
	disableSlett: Function
}

const validationSchema = Yup.object().shape(
	{
		telefonnummer: ifPresent('telefonnummer', Yup.array().of(telefonnummer)),
	},
	[['telefonnummer', 'telefonnummer']],
)

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
	const getRedigertAttributtListe = () => {
		const liste = [] as Array<any>
		initialValuesListe.forEach((item: any) => {
			const found = _.get(redigertAttributt, path)?.find(
				(redigertItem: any) => redigertItem.id === item.id,
			)
			if (found) {
				liste.push(found)
			} else {
				liste.push(null)
			}
		})
		return { [path]: liste }
	}

	const initialValuesListe = _.get(initialValues, path)
	const redigertAttributtListe = redigertAttributt && getRedigertAttributtListe()

	const disableIdx = disableSlett(_.get(redigertAttributtListe, path) || initialValuesListe)

	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [slettId, setSlettId] = useState(null)
	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: redigertAttributt ? redigertAttributtListe : initialValues,
		resolver: yupResolver(validationSchema),
	})

	const openDeleteModal = (idx: number) => {
		setSlettId(idx)
		openModal()
	}

	const pdlfError = (error: any) => {
		error &&
			setErrorMessagePdlf(`Feil ved oppdatering av person: ${error.message || error.toString()}`)
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
			const id = null as unknown as number
			const itemData = _.get(data, path)?.filter((item: any) => item)
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
			const id = _.get(initialValues, `${path}[${idx}].id`)
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

	return (
		<>
			<FormProvider {...formMethods}>
				<RedigerLoading visningModus={visningModus} />
				{[Modus.Les, Modus.LoadingPdlfSlett, Modus.LoadingPdlSlett].includes(visningModus) && (
					<DollyFieldArray data={initialValuesListe} header="" nested>
						{(item: any, idx: number) => {
							const redigertItem = _.get(redigertAttributtListe, `${path}.[${idx}]`)
							const slettetItem = redigertAttributtListe && !redigertItem

							return (
								<React.Fragment key={idx}>
									{visningModus === Modus.LoadingPdlfSlett && slettId === idx && (
										<Loading label={'Oppdaterer personinfo...'} />
									)}
									{visningModus === Modus.LoadingPdlSlett && slettId === idx && (
										<Loading label={'Oppdaterer PDL...'} />
									)}
									{(visningModus === Modus.Les || slettId !== idx) && (
										<>
											{slettetItem || alleSlettet ? (
												<OpplysningSlettet />
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
																<NavButton onClick={closeModal} variant={'secondary'}>
																	Nei
																</NavButton>

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
					<Form onSubmit={(data) => handleSubmit(data)} enableReinitialize>
						<>
							<DollyFieldArray
								data={_.get(redigertAttributtListe, path) || initialValuesListe}
								header=""
								nested
							>
								{(item: any, idx: number) =>
									item ? getForm(`${path}[${idx}]`) : <OpplysningSlettet />
								}
							</DollyFieldArray>
							<FieldArrayEdit>
								<Knappegruppe>
									<NavButton
										variant={'secondary'}
										onClick={() => setVisningModus(Modus.Les)}
										disabled={formMethods.formState.isSubmitting}
									>
										Avbryt
									</NavButton>
									<NavButton
										variant={'primary'}
										onClick={() => handleSubmit(formMethods.watch())}
										disabled={!formMethods.formState.isValid || formMethods.formState.isSubmitting}
									>
										Endre
									</NavButton>
								</Knappegruppe>
							</FieldArrayEdit>
						</>
					</Form>
				)}
			</FormProvider>
		</>
	)
}
