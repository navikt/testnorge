import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import * as _ from 'lodash-es'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import { deleteSkjerming } from '@/utils/hooks/useSkjerming'
import {
	folkeregisterpersonstatus,
	kjoenn,
	navn,
} from '@/components/fagsystem/pdlf/form/validation/validation'
import { ifPresent } from '@/utils/YupValidations'
import { PersondetaljerSamlet } from '@/components/fagsystem/pdlf/form/partials/persondetaljerSamlet/PersondetaljerSamlet'
import {
	Modus,
	RedigerLoading,
} from '@/components/fagsystem/pdlf/visning/visningRedigerbar/RedigerLoading'
import { Button as AkselButton, Checkbox, CheckboxGroup, Dialog, InfoCard } from '@navikt/ds-react'
import { InformationSquareFillIcon, PencilIcon, TrashIcon } from '@navikt/aksel-icons'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { usePdlForvalterPerson } from '@/utils/hooks/usePdlForvalter'

type VisningTypes = {
	dataVisning: any
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
	identtype?: string
	tpsMessagingData?: any
}

const validationSchema = Yup.object().shape(
	{
		folkeregisterpersonstatus: ifPresent(
			'folkeregisterpersonstatus',
			Yup.array().of(folkeregisterpersonstatus),
		),
		kjoenn: ifPresent('kjoenn', Yup.array().of(kjoenn)),
		navn: ifPresent('navn', Yup.array().of(navn)),
	},
	[
		['folkeregisterpersonstatus', 'folkeregisterpersonstatus'],
		['kjoenn', 'kjoenn'],
		['navn', 'navn'],
	],
)

const PersondetaljerVisning = styled.div`
	width: 100%;
	position: relative;
`

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 0;
	top: 0;
	margin: -5px 10px 0 0;
	display: flex;
	flex-direction: column;
	gap: var(--ax-space-4);

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

const initialSlettAttr = {
	navn: false,
	kjoenn: false,
	folkeregisterpersonstatus: false,
	skjerming: false,
}

type SlettAttr = typeof initialSlettAttr

export const VisningRedigerbarPersondetaljer = ({
	dataVisning,
	initialValues,
	redigertAttributt = null,
	ident,
	tpsMessagingData,
	identtype,
}: VisningTypes) => {
	const mutate = useMatchMutate()
	const { refresh: refreshPdlForvalter } = usePdlForvalterPerson(ident)
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [errorMessageSkjerming, setErrorMessageSkjerming] = useState(null)
	const [slettModalOpen, setSlettModalOpen] = useState(false)
	const [slettAttr, setSlettAttr] = useState<SlettAttr>(initialSlettAttr)
	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: redigertAttributt.pdlf ? redigertAttributt.pdlf : initialValues,
		resolver: yupResolver(validationSchema),
	})

	const pdlfError = (error: any) => {
		error &&
			setErrorMessagePdlf(`Feil ved oppdatering av person: ${error.message || error.toString()}`)
		setVisningModus(Modus.Les)
	}

	const pdlError = (error: any) => {
		error && setErrorMessagePdl(`Feil ved oppdatering i PDL: ${error.message || error.toString()}`)
		setVisningModus(Modus.Les)
	}

	const skjermingError = (error: any) => {
		error &&
			setErrorMessageSkjerming(
				`Feil ved sletting i skjermingsregisteret: ${error.message || error.toString()}`,
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
						? _.get(redigertAttributt, `${attr}[0]`)
						: _.get(initialValues, `${attr}[0]`)
					const itemData = _.get(data, `${attr}[0]`)

					if (!_.isEqual(itemData, initialData)) {
						await PdlforvalterApi.putAttributt(ident, attr, itemData?.id || 0, itemData).catch(
							(error) => {
								pdlfError(error)
								harFeil = true
							},
						)
					}
				}
			}
			return editFn()
				.then(() => {
					if (!harFeil) {
						setVisningModus(Modus.LoadingPdl)
						DollyApi.sendOrdre(ident).then(() => {
							refreshPdlForvalter().then(() => {
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
							const fornavn = dataVisning?.props?.person?.navn?.[0]?.fornavn
							const etternavn = dataVisning?.props?.person?.navn?.[0]?.etternavn
							skjerming.oppdatert = true
							setVisningModus(Modus.LoadingSkjerming)
							await deleteSkjerming(ident, fornavn, etternavn)
								.catch((error) => {
									skjermingError(error)
									skjerming.feil = true
								})
								.then(() => {
									console.log('Skjerming slettet')
									skjerming.oppdatert = false
									setVisningModus(Modus.Les)
									mutate(REGEX_BACKEND_GRUPPER)
									mutate(REGEX_BACKEND_BESTILLINGER)
									window.location.reload()
								})
						} else {
							pdlf.oppdatert = true
							setVisningModus(Modus.LoadingPdlf)
							const id = _.get(initialValues, `${attr}[0].id`)
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
							refreshPdlForvalter().then(() => {
								if (!skjerming.oppdatert || !skjerming.feil) {
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

	const harNavn = redigertAttributt?.pdlf
		? redigertAttributt?.pdlf?.navn?.[0]?.fornavn ||
			redigertAttributt?.pdlf?.navn?.[0]?.mellomnavn ||
			redigertAttributt?.pdlf?.navn?.[0]?.etternavn
		: initialValues?.navn?.[0]?.fornavn ||
			initialValues?.navn?.[0]?.mellomnavn ||
			initialValues?.navn?.[0]?.etternavn

	const harKjoenn = redigertAttributt?.pdlf
		? redigertAttributt?.pdlf?.kjoenn?.[0]?.kjoenn
		: initialValues?.kjoenn?.[0]?.kjoenn

	const harPersonstatus = redigertAttributt?.pdlf
		? redigertAttributt?.pdlf?.folkeregisterpersonstatus?.[0]?.status
		: initialValues?.folkeregisterpersonstatus?.[0]?.status

	const harSkjerming = redigertAttributt?.skjermingsregister
		? redigertAttributt?.skjermingsregister?.skjermetFra
		: initialValues?.skjermingsregister?.skjermetFra

	const valgteSlettAttr = Object.keys(slettAttr).filter((key) => slettAttr[key as keyof SlettAttr])

	const handleSlettAttrChange = (valgte: string[]) => {
		setSlettAttr({
			navn: valgte.includes('navn'),
			kjoenn: valgte.includes('kjoenn'),
			folkeregisterpersonstatus: valgte.includes('folkeregisterpersonstatus'),
			skjerming: valgte.includes('skjerming'),
		})
	}

	const handleSlettModalOpenChange = (open: boolean) => {
		setSlettModalOpen(open)
		if (!open) {
			setSlettAttr(initialSlettAttr)
		}
	}

	const slettModal = (
		<Dialog open={slettModalOpen} onOpenChange={handleSlettModalOpenChange}>
			<Dialog.Trigger>
				<AkselButton size="xsmall" variant="tertiary" icon={<TrashIcon title="Slett" />} />
			</Dialog.Trigger>
			<Dialog.Popup closeOnOutsideClick={false}>
				<Dialog.Header>
					<Dialog.Title>Slett opplysninger</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<CheckboxGroup
						legend="Hvilke opplysninger ønsker du å slette?"
						className="checkbox-group--aksel-default"
						value={valgteSlettAttr}
						onChange={handleSlettAttrChange}
					>
						{harNavn && <Checkbox value="navn">Navn</Checkbox>}
						{harKjoenn && <Checkbox value="kjoenn">Kjønn</Checkbox>}
						{harPersonstatus && <Checkbox value="folkeregisterpersonstatus">Personstatus</Checkbox>}
						{harSkjerming && <Checkbox value="skjerming">Skjerming (egenansatt)</Checkbox>}
					</CheckboxGroup>
					{slettAttr.skjerming && (
						<InfoCard data-color="info" size="small" style={{ marginTop: '1rem' }}>
							<InfoCard.Message icon={<InformationSquareFillIcon aria-hidden />}>
								Sletting av skjerming sletter all data på person i skjermingregisteret. For å heller
								avslutte skjermingen bruk "LEGG TIL/ENDRE" for å legge til en sluttdato for
								skjermingen.
							</InfoCard.Message>
						</InfoCard>
					)}
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<AkselButton variant="secondary" data-color="neutral">
							Avbryt
						</AkselButton>
					</Dialog.CloseTrigger>
					<AkselButton
						variant="danger"
						disabled={valgteSlettAttr.length === 0}
						onClick={() => {
							const attrSomSkalSlettes = slettAttr
							handleSlettModalOpenChange(false)
							return handleDelete(attrSomSkalSlettes)
						}}
					>
						Slett
					</AkselButton>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)

	return (
		<>
			<RedigerLoading visningModus={visningModus} />
			{visningModus === Modus.Les && (
				<PersondetaljerVisning>
					{dataVisning}
					<EditDeleteKnapper>
						<AkselButton
							size="xsmall"
							variant="tertiary"
							icon={<PencilIcon title="Endre" />}
							onClick={() => setVisningModus(Modus.Skriv)}
						/>
						{(harNavn || harKjoenn || harPersonstatus || harSkjerming) && slettModal}
					</EditDeleteKnapper>
					<div className="flexbox--full-width">
						{errorMessagePdlf && <div className="error-message">{errorMessagePdlf}</div>}
						{errorMessagePdl && <div className="error-message">{errorMessagePdl}</div>}
						{errorMessageSkjerming && <div className="error-message">{errorMessageSkjerming}</div>}
					</div>
				</PersondetaljerVisning>
			)}
			{visningModus === Modus.Skriv && (
				<FormProvider {...formMethods}>
					<Form onSubmit={(data) => handleSubmit(data)}>
						<>
							<div className="flexbox--flex-wrap">
								<PersondetaljerSamlet
									formMethods={formMethods}
									tpsMessaging={tpsMessagingData}
									harSkjerming={harSkjerming}
									identtype={identtype}
								/>
							</div>
							<Knappegruppe>
								<NavButton
									variant={'secondary'}
									style={{ marginRight: '10px' }}
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
						</>
					</Form>
				</FormProvider>
			)}
		</>
	)
}
