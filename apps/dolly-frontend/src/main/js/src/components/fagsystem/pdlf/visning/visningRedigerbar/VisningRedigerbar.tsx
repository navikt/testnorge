import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import { Formik, FormikProps } from 'formik'
import { FoedselForm } from '@/components/fagsystem/pdlf/form/partials/foedsel/Foedsel'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'
import * as _ from 'lodash-es'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import Icon from '@/components/ui/icon/Icon'
import DollyModal from '@/components/ui/modal/DollyModal'
import useBoolean from '@/utils/hooks/useBoolean'
import { StatsborgerskapForm } from '@/components/fagsystem/pdlf/form/partials/statsborgerskap/Statsborgerskap'
import { DoedsfallForm } from '@/components/fagsystem/pdlf/form/partials/doedsfall/Doedsfall'
import { InnvandringForm } from '@/components/fagsystem/pdlf/form/partials/innvandring/Innvandring'
import { UtvandringForm } from '@/components/fagsystem/pdlf/form/partials/utvandring/Utvandring'
import { BostedsadresseForm } from '@/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'
import { OppholdsadresseForm } from '@/components/fagsystem/pdlf/form/partials/adresser/oppholdsadresse/Oppholdsadresse'
import { KontaktadresseForm } from '@/components/fagsystem/pdlf/form/partials/adresser/kontaktadresse/Kontaktadresse'
import { VergemaalForm } from '@/components/fagsystem/pdlf/form/partials/vergemaal/Vergemaal'
import { FullmaktForm } from '@/components/fagsystem/pdlf/form/partials/fullmakt/Fullmakt'
import { SivilstandForm } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/sivilstand/Sivilstand'
import {
	AdressebeskyttelseForm,
	getIdenttype,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressebeskyttelse/Adressebeskyttelse'
import { doedsfall, navn } from '@/components/fagsystem/pdlf/form/validation/validation'
import {
	innflytting,
	statsborgerskap,
	utflytting,
	adressebeskyttelse,
	bostedsadresse,
	kontaktadresse,
	oppholdsadresse,
	vergemaal,
	fullmakt,
	sivilstand,
	kontaktDoedsbo,
	forelderBarnRelasjon,
	doedfoedtBarn,
	utenlandskId,
} from '@/components/fagsystem/pdlf/form/validation/partials'
import { ifPresent, validate } from '@/utils/YupValidations'
import {
	RedigerLoading,
	Modus,
} from '@/components/fagsystem/pdlf/visning/visningRedigerbar/RedigerLoading'
import { Option } from '@/service/SelectOptionsOppslag'
import { KontaktinformasjonForDoedsboForm } from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'
import { NavnForm } from '@/components/fagsystem/pdlf/form/partials/navn/Navn'
import { ForelderBarnRelasjonForm } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/ForelderBarnRelasjon'
import { ForeldreansvarForm } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/foreldreansvar/Foreldreansvar'
import {
	deltBosted,
	foreldreansvarForBarn,
} from '@/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'
import { DeltBostedForm } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/DeltBosted'
import { DoedfoedtBarnForm } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/doedfoedtBarn/DoedfoedtBarn'
import { UtenlandsIdForm } from '@/components/fagsystem/pdlf/form/partials/identifikasjon/utenlandsId/UtenlandsId'

type VisningTypes = {
	getPdlForvalter: Function
	dataVisning: any
	initialValues: any
	eksisterendeNyPerson?: Option
	redigertAttributt?: any
	path: string
	ident: string
	identtype?: string
	disableSlett?: boolean
	personFoerLeggTil?: any
}

enum Attributt {
	Navn = 'navn',
	Foedsel = 'foedsel',
	Doedsfall = 'doedsfall',
	Statsborgerskap = 'statsborgerskap',
	Innvandring = 'innflytting',
	Utvandring = 'utflytting',
	Vergemaal = 'vergemaal',
	Fullmakt = 'fullmakt',
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
	eksisterendeNyPerson = null,
	redigertAttributt = null,
	path,
	ident,
	identtype,
	disableSlett = false,
	personFoerLeggTil = null,
	personValues = null,
	relasjoner = null,
	relatertPersonInfo = null,
}: VisningTypes) => {
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

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
		const itemData = _.get(data, path)
		return PdlforvalterApi.putAttributt(ident, path?.toLowerCase(), id, itemData)
			.catch((error: Error) => {
				pdlfError(error)
			})
			.then((putResponse: any) => {
				if (putResponse) {
					setVisningModus(Modus.LoadingPdl)
					DollyApi.sendOrdre(ident).then(() => {
						getPdlForvalter().then(() => {
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
						getPdlForvalter().then(() => {
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

	const handleSubmitRelatertPerson = useCallback((data: any, ident: string) => {
		const submit = async () => {
			setVisningModus(Modus.LoadingPdlf)
			await PdlforvalterApi.setStandalone(ident).then((importResponse) => {
				if (importResponse?.ok) {
					sendData(data)
				}
			})
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
			await PdlforvalterApi.setStandalone(relatertPersonInfo?.ident)?.then((importResponse) => {
				if (importResponse?.ok) {
					sendSlett()
				}
			})
		}
		mountedRef.current = false
		return slett()
	}, [])

	const getForm = (formikBag: FormikProps<{}>) => {
		switch (path) {
			case Attributt.Navn:
				return <NavnForm formikBag={formikBag} path={path} />
			case Attributt.Foedsel:
				return <FoedselForm formikBag={formikBag} path={path} />
			case Attributt.Doedsfall:
				return <DoedsfallForm path={path} />
			case Attributt.Statsborgerskap:
				return <StatsborgerskapForm path={path} />
			case Attributt.Innvandring:
				return <InnvandringForm path={path} />
			case Attributt.Utvandring:
				return <UtvandringForm path={path} />
			case Attributt.Vergemaal:
				return (
					<VergemaalForm
						formikBag={formikBag}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.Fullmakt:
				return (
					<FullmaktForm
						formikBag={formikBag}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.Boadresse:
				return <BostedsadresseForm formikBag={formikBag} path={path} identtype={identtype} />
			case Attributt.Oppholdsadresse:
				return <OppholdsadresseForm formikBag={formikBag} path={path} />
			case Attributt.Kontaktadresse:
				return <KontaktadresseForm formikBag={formikBag} path={path} />
			case Attributt.Adressebeskyttelse:
				return (
					<AdressebeskyttelseForm
						formikBag={formikBag}
						path={path}
						identtype={getIdenttype(formikBag, identtype)}
					/>
				)
			case Attributt.DeltBosted:
				return (
					<DeltBostedForm
						formikBag={formikBag}
						path={path}
						relasjoner={relasjoner}
						personValues={personValues}
					/>
				)
			case Attributt.Sivilstand:
				return (
					<SivilstandForm
						path={path}
						formikBag={formikBag}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.KontaktinformasjonForDoedsbo:
				return (
					<KontaktinformasjonForDoedsboForm
						formikBag={formikBag}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.ForelderBarnRelasjon:
				return (
					<ForelderBarnRelasjonForm
						formikBag={formikBag}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.Foreldreansvar:
				return (
					<ForeldreansvarForm
						formikBag={formikBag}
						path={path}
						eksisterendeNyPerson={eksisterendeNyPerson}
					/>
				)
			case Attributt.DoedfoedtBarn:
				return <DoedfoedtBarnForm formikBag={formikBag} path={path} />
			case Attributt.UtenlandskIdentifikasjonsnummer:
				return <UtenlandsIdForm path={path} />
		}
	}

	const validationSchema = Yup.object().shape(
		{
			navn: ifPresent('navn', navn),
			doedsfall: ifPresent('doedsfall', doedsfall),
			statsborgerskap: ifPresent('statsborgerskap', statsborgerskap),
			innflytting: ifPresent('innflytting', innflytting),
			utflytting: ifPresent('utflytting', utflytting),
			vergemaal: ifPresent('vergemaal', vergemaal),
			fullmakt: ifPresent('fullmakt', fullmakt),
			bostedsadresse: ifPresent('bostedsadresse', bostedsadresse),
			oppholdsadresse: ifPresent('oppholdsadresse', oppholdsadresse),
			kontaktadresse: ifPresent('kontaktadresse', kontaktadresse),
			adressebeskyttelse: ifPresent('adressebeskyttelse', adressebeskyttelse),
			deltBosted: ifPresent('deltBosted', deltBosted),
			sivilstand: ifPresent('sivilstand', sivilstand),
			kontaktinformasjonForDoedsbo: ifPresent('kontaktinformasjonForDoedsbo', kontaktDoedsbo),
			forelderBarnRelasjon: ifPresent('forelderBarnRelasjon', forelderBarnRelasjon),
			foreldreansvar: ifPresent(
				'foreldreansvar',
				Yup.mixed().when('foreldreansvar', {
					is: (foreldreansvar) => Array.isArray(foreldreansvar),
					then: () => Yup.array().of(foreldreansvarForBarn),
					otherwise: () => foreldreansvarForBarn,
				})
			),
			doedfoedtBarn: ifPresent('doedfoedtBarn', doedfoedtBarn),
			utenlandskIdentifikasjonsnummer: ifPresent('utenlandskIdentifikasjonsnummer', utenlandskId),
		},
		[
			['navn', 'navn'],
			['doedsfall', 'doedsfall'],
			['statsborgerskap', 'statsborgerskap'],
			['innflytting', 'innflytting'],
			['utflytting', 'utflytting'],
			['vergemaal', 'vergemaal'],
			['fullmakt', 'fullmakt'],
			['bostedsadresse', 'bostedsadresse'],
			['oppholdsadresse', 'oppholdsadresse'],
			['kontaktadresse', 'kontaktadresse'],
			['adressebeskyttelse', 'adressebeskyttelse'],
			['deltBosted', 'deltBosted'],
			['sivilstand', 'sivilstand'],
			['kontaktinformasjonForDoedsbo', 'kontaktinformasjonForDoedsbo'],
			['forelderBarnRelasjon', 'forelderBarnRelasjon'],
			['foreldreansvar', 'foreldreansvar'],
			['doedfoedtBarn', 'doedfoedtBarn'],
			['utenlandskIdentifikasjonsnummer', 'utenlandskIdentifikasjonsnummer'],
		]
	)

	const _validate = (values: any) =>
		validate(
			{
				...values,
				personFoerLeggTil: personFoerLeggTil,
				personValues: personValues,
			},
			validationSchema
		)

	return (
		<>
			<RedigerLoading visningModus={visningModus} />
			{visningModus === Modus.Les && (
				<>
					{dataVisning}
					<EditDeleteKnapper>
						<Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} title="Endre" />
						<Button
							kind="trashcan"
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
				<Formik
					initialValues={redigertAttributt ? redigertAttributt : initialValues}
					onSubmit={(data) =>
						relatertPersonInfo?.ident
							? handleSubmitRelatertPerson(data, relatertPersonInfo.ident)
							: handleSubmit(data)
					}
					enableReinitialize
					validate={_validate}
				>
					{(formikBag) => {
						return (
							<>
								<FieldArrayEdit>
									<div className="flexbox--flex-wrap">{getForm(formikBag)}</div>
									<Knappegruppe>
										<NavButton
											variant="secondary"
											onClick={() => setVisningModus(Modus.Les)}
											disabled={formikBag.isSubmitting}
										>
											Avbryt
										</NavButton>
										<NavButton
											variant="primary"
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
