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
import { InnvandringForm } from '~/components/fagsystem/pdlf/form/partials/innvandring/Innvandring'
import { UtvandringForm } from '~/components/fagsystem/pdlf/form/partials/utvandring/Utvandring'
import { BostedsadresseForm } from '~/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'
import { OppholdsadresseForm } from '~/components/fagsystem/pdlf/form/partials/adresser/oppholdsadresse/Oppholdsadresse'
import { KontaktadresseForm } from '~/components/fagsystem/pdlf/form/partials/adresser/kontaktadresse/Kontaktadresse'
import { AdressebeskyttelseForm } from '~/components/fagsystem/pdlf/form/partials/adresser/adressebeskyttelse/Adressebeskyttelse'
import {
	doedsfall,
	innflytting,
	statsborgerskap,
	utflytting,
	adressebeskyttelse,
	bostedsadresse,
	kontaktadresse,
	oppholdsadresse,
} from '~/components/fagsystem/pdlf/form/validation'
import { ifPresent } from '~/utils/YupValidations'

type VisningTypes = {
	getPdlForvalter: Function
	dataVisning: any
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
	identtype?: string
	slettDisabled?: boolean
}

enum Modus {
	Les = 'LES',
	Skriv = 'SKRIV',
	LoadingPdlf = 'LOADING_PDLF',
	LoadingPdl = 'LOADING_PDL',
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
	identtype,
	slettDisabled = false,
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
		const submit = async () => {
			const id = _get(data, `${path}.id`)
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

	const getForm = (formikBag: FormikProps<{}>) => {
		switch (path) {
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
			case Attributt.Boadresse:
				return <BostedsadresseForm formikBag={formikBag} path={path} identtype={identtype} />
			case Attributt.Oppholdsadresse:
				return <OppholdsadresseForm formikBag={formikBag} path={path} />
			case Attributt.Kontaktadresse:
				return <KontaktadresseForm formikBag={formikBag} path={path} />
			case Attributt.Adressebeskyttelse:
				return <AdressebeskyttelseForm formikBag={formikBag} path={path} identtype={identtype} />
		}
	}

	const validationSchema = Yup.object().shape(
		{
			doedsfall: ifPresent('doedsfall', doedsfall),
			statsborgerskap: ifPresent('statsborgerskap', statsborgerskap),
			innflytting: ifPresent('innflytting', innflytting),
			utflytting: ifPresent('utflytting', utflytting),
			bostedsadresse: ifPresent('bostedsadresse', bostedsadresse),
			oppholdsadresse: ifPresent('oppholdsadresse', oppholdsadresse),
			kontaktadresse: ifPresent('kontaktadresse', kontaktadresse),
			adressebeskyttelse: ifPresent('adressebeskyttelse', adressebeskyttelse),
		},
		[
			['doedsfall', 'doedsfall'],
			['statsborgerskap', 'statsborgerskap'],
			['innflytting', 'innflytting'],
			['utflytting', 'utflytting'],
			['bostedsadresse', 'bostedsadresse'],
			['oppholdsadresse', 'oppholdsadresse'],
			['kontaktadresse', 'kontaktadresse'],
			['adressebeskyttelse', 'adressebeskyttelse'],
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
						<Button
							kind="trashcan"
							onClick={() => openModal()}
							title="Slett"
							disabled={slettDisabled}
						/>
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
						return (
							<>
								<FieldArrayEdit>
									<div className="flexbox--flex-wrap">{getForm(formikBag)}</div>
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
