import * as Yup from 'yup'
import { ifPresent, requiredBoolean, requiredString } from '@/utils/YupValidations'
import * as _ from 'lodash-es'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { Option, SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { FormikProps } from 'formik'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { useState } from 'react'
import { lookup } from 'country-data-list'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { AdresseKodeverk } from '@/config/kodeverk'

type KrrstubFormProps = {
	formikBag: FormikProps<{}>
}

type Change = {
	value: boolean
}

export const krrAttributt = 'krrstub'

export const KrrstubForm = ({ formikBag }: KrrstubFormProps) => {
	const { kodeverk: landkoder, loading } = useKodeverk(AdresseKodeverk.ArbeidOgInntektLand)
	const [land, setLand] = useState(_.get(formikBag.values, 'krrstub.land') || 'NO')
	const [mobilnummer, setMobilnummer] = useState(_.get(formikBag, 'values.krrstub.mobil') || '')
	const leverandoerer = SelectOptionsOppslag.hentKrrLeverandoerer()

	const mergedeLandkoder = landkoder?.map((landkode: Option) => {
		const lookupLand = lookup.countries({ alpha2: landkode.value })?.[0]
		return {
			countryCallingCodes: lookupLand?.countryCallingCodes[0],
			emoji: lookupLand?.emoji,
			...landkode,
		}
	})
	const telefonLandkoder = SelectOptionsFormat.formatOptions('telefonLandkoder', mergedeLandkoder)
	const leverandoerOptions = SelectOptionsFormat.formatOptions('sdpLeverandoer', leverandoerer)
	const registrert = _.get(formikBag, 'values.krrstub.registrert')

	const handleRegistrertChange = (newRegistrert: Change) => {
		if (!newRegistrert.value) {
			formikBag.setFieldValue('krrstub', {
				registrert: newRegistrert.value,
			})
		} else {
			formikBag.setFieldValue('krrstub', {
				epost: '',
				gyldigFra: null,
				landkode: '+47',
				mobil: '',
				sdpAdresse: '',
				sdpLeverandoer: '',
				spraak: '',
				registrert: newRegistrert.value,
				reservert: null,
			})
		}
	}

	const gyldigMobilnummer = () => {
		const landkode = _.get(formikBag.values, 'krrstub.landkode')
		if (mobilnummer.length === 0) {
			return { feilmelding: 'Feltet er påkrevd' }
		}
		if (landkode === '+47') {
			return mobilnummer.length === 8 && mobilnummer.match('^[0-9]+$')
				? null
				: { feilmelding: 'Telefonnummer må ha 8 siffer' }
		} else {
			return mobilnummer.length > 3 && mobilnummer.length < 12 && mobilnummer.match('^[0-9]+$')
				? null
				: { feilmelding: 'Telefonnummer må ha mellom 4 og 11 siffer' }
		}
	}

	const mobilnummerFeil = gyldigMobilnummer()

	return (
		//@ts-ignore
		<Vis attributt={krrAttributt}>
			<Panel
				heading="Kontakt- og reservasjonsregisteret"
				hasErrors={panelError(formikBag, krrAttributt)}
				iconType="krr"
				startOpen={erForsteEllerTest(formikBag.values, [krrAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<DollySelect
						name="krrstub.registrert"
						label="Registrert i KRR"
						options={Options('boolean')}
						onChange={handleRegistrertChange}
						value={registrert}
						isClearable={false}
						feil={registrert === null && { feilmelding: 'Feltet er påkrevd' }}
					/>
					{registrert && (
						<>
							<FormikSelect
								name="krrstub.reservert"
								label="Reservert"
								options={Options('boolean')}
								fastfield={false}
							/>
							<FormikTextInput name="krrstub.epost" label="E-post" />
							<div
								style={{
									display: 'flex',
									width: '-webkit-fill-available',
								}}
							>
								<DollySelect
									name="krrstub.landkode"
									value={land}
									isLoading={loading}
									fastfield={false}
									options={telefonLandkoder}
									label={'Landkode'}
									onChange={(option: Option) => {
										setLand(option.value)
										formikBag.setFieldValue('krrstub.landkode', option.landkode)
										formikBag.setFieldValue('krrstub.land', option.value)
									}}
									isClearable={false}
									size={'xlarge'}
								/>
								<DollyTextInput
									name="krrstub.mobil"
									label="Mobilnummer"
									value={mobilnummer}
									size={'medium'}
									//TODO: Naar React Hook Form er implementert, flytt gyldigMobilnummer() til Yup-validering
									feil={mobilnummerFeil}
									onChange={(event) => {
										setMobilnummer(event.target.value)
										formikBag.setFieldValue('krrstub.mobil', event.target.value)
									}}
								/>
							</div>
							<FormikSelect
								name="krrstub.spraak"
								label="Språk"
								options={Options('spraaktype')}
								fastfield={false}
							/>
							<FormikDatepicker
								name="krrstub.gyldigFra"
								label="Kontaktinfo gjelder fra"
								fastfield={false}
							/>
						</>
					)}
				</div>
				{registrert && (
					<Kategori title={'Sikker digital postkasse'}>
						<div className="flexbox--flex-wrap">
							<FormikTextInput name="krrstub.sdpAdresse" label="Adresse" />
							<FormikSelect
								fastfield={false}
								name="krrstub.sdpLeverandoer"
								label="Leverandør"
								options={leverandoerOptions}
							/>
						</div>
					</Kategori>
				)}
			</Panel>
		</Vis>
	)
}

const testMobil = (val) => {
	return val.test('gyldig-mobil', 'Ugyldig telefonnummer', function isValid(mobil) {
		const values = this.options.context
		const registrert = _.get(values, 'krrstub.registrert')
		const landkode = _.get(values, 'krrstub.landkode')
		if (!registrert) {
			return true
		}
		if (mobil.length === 0) {
			return false
		}
		if (landkode === '+47') {
			return mobil.length === 8 && mobil.match('^[0-9]+$')
		} else {
			return mobil.length > 3 && mobil.length < 12 && mobil.match('^[0-9]+$')
		}
	})
}

KrrstubForm.validation = {
	krrstub: ifPresent(
		'$krrstub',
		Yup.object({
			epost: Yup.string(),
			gyldigFra: Yup.date().nullable(),
			landkode: Yup.mixed().when('registrert', {
				is: (registrert) => registrert,
				then: () => requiredString,
				otherwise: () => Yup.mixed().nullable(),
			}),
			mobil: testMobil(Yup.string().nullable()),
			sdpAdresse: Yup.string(),
			sdpLeverandoer: Yup.string().nullable(),
			spraak: Yup.string(),
			registrert: ifPresent('$krrstub.registrert', requiredBoolean),
			reservert: Yup.boolean().nullable(),
		}),
	),
}
