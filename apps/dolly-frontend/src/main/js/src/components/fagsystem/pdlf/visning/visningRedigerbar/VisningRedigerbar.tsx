import React, { lazy, Suspense, useCallback, useRef, useState } from 'react'
import { FoedselForm } from '@/components/fagsystem/pdlf/form/partials/foedsel/Foedsel'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'
import * as _ from 'lodash-es'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import Icon from '@/components/ui/icon/Icon'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import useBoolean from '@/utils/hooks/useBoolean'
import { StatsborgerskapForm } from '@/components/fagsystem/pdlf/form/partials/statsborgerskap/Statsborgerskap'
import { DoedsfallForm } from '@/components/fagsystem/pdlf/form/partials/doedsfall/Doedsfall'
import { InnvandringForm } from '@/components/fagsystem/pdlf/form/partials/innvandring/Innvandring'
import { UtvandringForm } from '@/components/fagsystem/pdlf/form/partials/utvandring/Utvandring'
import { BostedsadresseForm } from '@/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'
import { OppholdsadresseForm } from '@/components/fagsystem/pdlf/form/partials/adresser/oppholdsadresse/Oppholdsadresse'
import { KontaktadresseForm } from '@/components/fagsystem/pdlf/form/partials/adresser/kontaktadresse/Kontaktadresse'
import { VergemaalForm } from '@/components/fagsystem/pdlf/form/partials/vergemaal/Vergemaal'
import { SivilstandForm } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/sivilstand/Sivilstand'
import {
	AdressebeskyttelseForm,
	getIdenttype
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressebeskyttelse/Adressebeskyttelse'
import { Modus, RedigerLoading } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/RedigerLoading'
import { Option } from '@/service/SelectOptionsOppslag'
import {
	KontaktinformasjonForDoedsboForm
} from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'
import { NavnForm } from '@/components/fagsystem/pdlf/form/partials/navn/Navn'
import {
	ForelderBarnRelasjonForm
} from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/ForelderBarnRelasjon'
import {
	ForeldreansvarForm
} from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/foreldreansvar/Foreldreansvar'
import {
	DeltBostedForm
} from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/DeltBosted'
import {
	DoedfoedtBarnForm
} from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/doedfoedtBarn/DoedfoedtBarn'
import { UtenlandsIdForm } from '@/components/fagsystem/pdlf/form/partials/identifikasjon/utenlandsId/UtenlandsId'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { Form, FormProvider, useForm } from 'react-hook-form'
import {
	visningRedigerbarValidation
} from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarValidation'
import { yupResolver } from '@hookform/resolvers/yup'
import './VisningRedigerbarForm.less'
import { FoedestedForm } from '@/components/fagsystem/pdlf/form/partials/foedsel/Foedested'
import { FoedselsdatoForm } from '@/components/fagsystem/pdlf/form/partials/foedsel/Foedselsdato'
import { devEnabled } from '@/components/bestillingsveileder/stegVelger/StegVelger'
import { PersonstatusForm } from '@/components/fagsystem/pdlf/form/partials/personstatus/Personstatus'
import { erDollyAdmin } from '@/utils/DollyAdmin'
import { usePdlForvalterPerson } from '@/utils/hooks/usePdlForvalter'

type VisningTypes = {
	dataVisning: any
	initialValues: any
	eksisterendeNyPerson?: Option
	redigertAttributt?: any
	path: string
	ident: string
	identMaster?: string
	identtype?: string
	disableSlett?: boolean
	personFoerLeggTil?: any
	personValues: any
	relasjoner: any
	relatertPersonInfo: any
	master: any
}

enum Attributt {
	Navn = 'navn',
	Personstatus = 'folkeregisterpersonstatus',
	Foedsel = 'foedsel',
	Foedested = 'foedested',
	Foedselsdato = 'foedselsdato',
	Doedsfall = 'doedsfall',
	Statsborgerskap = 'statsborgerskap',
	Innvandring = 'innflytting',
	Utvandring = 'utflytting',
	Vergemaal = 'vergemaal',
	Boadresse = 'bostedsadresse',
	Oppholdsadresse = 'oppholdsadresse',
	Kontaktadresse = 'kontaktadresse',
	Adressebeskyttelse = 'adressebeskyttelse',
	DeltBosted = 'deltBosted',
	Sivilstand = 'sivilstand',
	KontaktinformasjonForDoedsbo = 'kontaktinformasjonForDoedsbo',
	ForelderBarnRelasjon = 'forelderBarnRelasjon',
	Foreldreansvar = 'foreldreansvar',
	DoedfoedtBarn = 'doedfoedtBarn',
	UtenlandskIdentifikasjonsnummer = 'utenlandskIdentifikasjonsnummer',
}

const FieldArrayEdit = styled.div`
	&&& {
		.dolly-button {
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
	dataVisning,
	initialValues,
	eksisterendeNyPerson = null,
	redigertAttributt = null,
	path,
	ident,
	identtype,
	identMaster = '',
	disableSlett = false,
	personValues = null,
	relasjoner = null,
	relatertPersonInfo = null,
	master = null,
}: VisningTypes) => {
	const { refresh: refreshPdlForvalter } = usePdlForvalterPerson(ident)
	const DisplayFormState = lazy(() => import('@/utils/DisplayFormState'))
	const DisplayFormErrors = lazy(() => import('@/utils/DisplayFormErrors'))

	const visFormState = devEnabled || erDollyAdmin()
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: redigertAttributt ? redigertAttributt : initialValues,
		resolver: yupResolver(visningRedigerbarValidation),
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

	const sendData = (data) => {
		const id = _.get(data, `${path}.id`)
		const itemData = formMethods.watch(path)
		return PdlforvalterApi.putAttributt(ident, path?.toLowerCase(), id, itemData)
			.catch((error: Error) => {
				pdlfError(error)
			})
			.then((putResponse: any) => {
				if (putResponse) {
					setVisningModus(Modus.LoadingPdl)
					DollyApi.sendOrdre(ident).then(() => {
						refreshPdlForvalter().then(() => {
							setVisningModus(Modus.Les)
						})
					})
				}
			})
			.catch((error: Error) => {
				pdlError(error)
			})
	}

	const sendSlett = () => {
		const id = _.get(initialValues, `${path}.id`)
		return PdlforvalterApi.deleteAttributt(ident, path?.toLowerCase(), id)
			.catch((error: Error) => {
				pdlfError(error)
			})
			.then((deleteResponse: any) => {
				if (deleteResponse) {
					setVisningModus(Modus.LoadingPdl)
					DollyApi.sendOrdre(ident).then(() => {
						refreshPdlForvalter().then(() => {
							setVisningModus(Modus.Les)
						})
					})
				}
			})
			.catch((error: Error) => {
				pdlError(error)
			})
	}

	const mountedRef = useRef(true)

	const handleSubmit = useCallback((data: any) => {
		const submit = async () => {
			setVisningModus(Modus.LoadingPdlf)
			await sendData(data)
		}
		mountedRef.current = false
		return submit()
	}, [])

	const handleDelete = useCallback(() => {
		const slett = async () => {
			setVisningModus(Modus.LoadingPdlf)
			await sendSlett()
		}
		mountedRef.current = false
		return slett()
	}, [])

	const handleDeleteRelatertPerson = useCallback(() => {
		const slett = async () => {
			setVisningModus(Modus.LoadingPdlf)
			sendSlett()
		}
		mountedRef.current = false
		return slett()
	}, [])

	const onNavButtonClick = useCallback(() => {
		formMethods.reset()
		setVisningModus(Modus.Les)
	}, [])

	const getForm = (formMethods: UseFormReturn) => {
		switch (path) {
			case Attributt.Navn:
				return <NavnForm formMethods={formMethods} path={path} />
			case Attributt.Personstatus:
				return <PersonstatusForm path={path} />
			case Attributt.Foedsel:
				return <FoedselForm formMethods={formMethods} path={path} />
			case Attributt.Foedested:
				return <FoedestedForm formMethods={formMethods} path={path} />
			case Attributt.Foedselsdato:
				return <FoedselsdatoForm formMethods={formMethods} path={path} />
			case Attributt.Doedsfall:
				return <DoedsfallForm path={path} />
			case Attributt.Statsborgerskap:
				return (
					<StatsborgerskapForm
						path={path}
						kanVelgeMaster={identMaster !== 'PDL' && identtype !== 'NPID'}
					/>
				)
			case Attributt.Innvandring:
				return <InnvandringForm path={path} />
			case Attributt.Utvandring:
				return <UtvandringForm path={path} />
			case Attributt.Vergemaal:
				return (
					<VergemaalForm
						formMethods={formMethods}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.Boadresse:
				return (
					<BostedsadresseForm
						formMethods={formMethods}
						path={path}
						identtype={identtype}
						identMaster={master}
					/>
				)
			case Attributt.Oppholdsadresse:
				return (
					<OppholdsadresseForm
						formMethods={formMethods}
						path={path}
						identtype={identtype}
						identMaster={master}
					/>
				)
			case Attributt.Kontaktadresse:
				return (
					<KontaktadresseForm
						formMethods={formMethods}
						path={path}
						identtype={identtype}
						identMaster={master}
					/>
				)
			case Attributt.Adressebeskyttelse:
				return (
					<AdressebeskyttelseForm
						formMethods={formMethods}
						path={path}
						identtype={getIdenttype(formMethods, identtype)}
						identMaster={master}
					/>
				)
			case Attributt.DeltBosted:
				return (
					<DeltBostedForm
						formMethods={formMethods}
						path={path}
						relasjoner={relasjoner}
						personValues={personValues}
					/>
				)
			case Attributt.Sivilstand:
				return (
					<SivilstandForm
						path={path}
						formMethods={formMethods}
						eksisterendeNyPerson={eksisterendeNyPerson}
						identtype={identtype}
						ident={ident}
					/>
				)
			case Attributt.KontaktinformasjonForDoedsbo:
				return (
					<KontaktinformasjonForDoedsboForm
						formMethods={formMethods}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.ForelderBarnRelasjon:
				return (
					<ForelderBarnRelasjonForm
						formMethods={formMethods}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
						identtype={identtype}
						ident={ident}
					/>
				)
			case Attributt.Foreldreansvar:
				return (
					<ForeldreansvarForm
						formMethods={formMethods}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.DoedfoedtBarn:
				return <DoedfoedtBarnForm formMethods={formMethods} path={path} />
			case Attributt.UtenlandskIdentifikasjonsnummer:
				return <UtenlandsIdForm path={path} identtype={identtype} />
		}
	}

	return (
		<>
			<FormProvider {...formMethods}>
				<RedigerLoading visningModus={visningModus} />
				{visningModus === Modus.Les && (
					<>
						{dataVisning}
						<EditDeleteKnapper>
							<Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} title="Endre" />
							<Button
								kind="trashcan"
								fontSize={'1.4rem'}
								onClick={() => openModal()}
								title="Slett"
								disabled={disableSlett}
							/>
							<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
								<div className="slettModal">
									<div className="slettModal slettModal-content">
										<Icon size={50} kind="report-problem-circle" />
										<h1>Sletting</h1>
										<h4>Er du sikker på at du vil slette denne opplysningen fra personen?</h4>
									</div>
									<div className="slettModal-actions">
										<NavButton onClick={closeModal} variant="secondary">
											Nei
										</NavButton>
										<NavButton
											onClick={() => {
												closeModal()
												return relatertPersonInfo ? handleDeleteRelatertPerson() : handleDelete()
											}}
											variant="primary"
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
					<Form onSubmit={(data) => handleSubmit(data)}>
						<>
							{visFormState && (
								<>
									<Suspense>
										<DisplayFormState />
										<DisplayFormErrors errors={formMethods.formState.errors} label={'Vis errors'} />
									</Suspense>
								</>
							)}
							<FieldArrayEdit>
								<div className="flexbox--flex-wrap visning-redigerbar-form">
									{getForm(formMethods)}
								</div>
								<Knappegruppe>
									<NavButton
										variant="secondary"
										style={{ marginRight: '10px' }}
										onClick={onNavButtonClick}
										disabled={formMethods.formState.isSubmitting}
									>
										Avbryt
									</NavButton>
									<NavButton
										variant="primary"
										onClick={() => handleSubmit(formMethods.watch())}
										disabled={formMethods.formState.isSubmitting}
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
