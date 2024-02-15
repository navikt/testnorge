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
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { useState } from 'react'
import { lookup } from 'country-data-list'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { AdresseKodeverk } from '@/config/kodeverk'
import { useFormContext } from 'react-hook-form'

type Change = {
	value: boolean
}

export const krrAttributt = 'krrstub'

export const KrrstubForm = () => {
	const formMethods = useFormContext()
	const { kodeverk: landkoder, loading } = useKodeverk(AdresseKodeverk.ArbeidOgInntektLand)
	const [land, setLand] = useState(formMethods.watch('krrstub.land'))
	const [mobilnummer, setMobilnummer] = useState(formMethods.watch('values.krrstub.mobil') || '')
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
	const registrert = formMethods.watch('krrstub.registrert')

	const handleRegistrertChange = (newRegistrert: Change) => {
		if (!newRegistrert.value) {
			formMethods.setValue('krrstub', {
				registrert: newRegistrert.value,
			})
		} else {
			formMethods.setValue('krrstub', {
				epost: '',
				gyldigFra: null,
				landkode: '+47',
				mobil: null,
				sdpAdresse: '',
				sdpLeverandoer: '',
				spraak: '',
				registrert: newRegistrert.value,
				reservert: null,
				land: 'NO',
			})
			setLand('NO')
		}
	}

	const gyldigMobilnummer = () => {
		const landkode = formMethods.watch('krrstub.landkode')
		if (!landkode) {
			return null
		}
		if (!mobilnummer || mobilnummer?.length === 0) {
			formMethods.setError('krrstub.mobil', { message: 'Feltet er påkrevd' })
		}
		if (landkode === '+47') {
			if (mobilnummer?.length === 8 && mobilnummer?.match('^[0-9]+$')) {
				formMethods.clearErrors('krrstub.mobil')
			} else {
				formMethods.setError('krrstub.mobil', {
					message: 'Telefonnummer må ha 8 siffer',
				})
			}
		} else {
			formMethods.setValue('krrstub.registrert', true)
		}
		formMethods.trigger('krrstub')
	}

	const gyldigLandkode = () => {
		if (!mobilnummer || mobilnummer?.length === 0) {
			formMethods.clearErrors('krrstub.landkode')
		}
		if (formMethods.watch('krrstub.landkode')) {
			formMethods.clearErrors('krrstub.landkode')
		} else {
			formMethods.setError('krrstub.landkode', {
				message: 'Feltet er påkrevd',
			})
		}
	}

	return (
		//@ts-ignore
		<Vis attributt={krrAttributt}>
			<Panel
				heading="Kontakt- og reservasjonsregisteret"
				hasErrors={panelError(krrAttributt)}
				iconType="krr"
				startOpen={erForsteEllerTest(formMethods.getValues(), [krrAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<DollySelect
						name="krrstub.registrert"
						label="Registrert i KRR"
						options={Options('boolean')}
						onChange={handleRegistrertChange}
						value={registrert}
						isClearable={false}
					/>
					{registrert && (
						<>
							<FormikSelect
								name="krrstub.reservert"
								label="Reservert"
								options={Options('boolean')}
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
									options={telefonLandkoder}
									label={'Landkode'}
									onChange={(option: Option) => {
										setLand(option?.value)
										formMethods.setValue('krrstub.landkode', option?.landkode || null)
										formMethods.setValue('krrstub.land', option?.value || null)
										gyldigLandkode()
										gyldigMobilnummer()
									}}
									size={'xlarge'}
								/>
								<DollyTextInput
									name="krrstub.mobil"
									label="Mobilnummer"
									value={mobilnummer}
									size={'medium'}
									onChange={(event) => {
										setMobilnummer(event.target.value || null)
										formMethods.setValue('krrstub.mobil', event.target.value || null)
										gyldigLandkode()
										gyldigMobilnummer()
									}}
								/>
							</div>
							<FormikSelect name="krrstub.spraak" label="Språk" options={Options('spraaktype')} />
							<FormikDatepicker name="krrstub.gyldigFra" label="Kontaktinfo gjelder fra" />
						</>
					)}
				</div>
				{registrert && (
					<Kategori title={'Sikker digital postkasse'}>
						<div className="flexbox--flex-wrap">
							<FormikTextInput name="krrstub.sdpAdresse" label="Adresse" />
							<FormikSelect
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
	return val.test('gyldig-mobil', 'Ugyldig telefonnummer', (mobil, testContext) => {
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		const registrert = _.get(fullForm, 'krrstub.registrert')
		const landkode = _.get(fullForm, 'krrstub.landkode')
		if (!registrert || !landkode) {
			return true
		}
		if (landkode && mobil?.length === 0) {
			return false
		}
		if (landkode === '+47') {
			return mobil?.length === 8 && mobil?.match('^[0-9]+$')
		} else {
			return mobil?.length > 3 && mobil?.length < 12 && mobil.match('^[0-9]+$')
		}
	})
}

KrrstubForm.validation = {
	krrstub: ifPresent(
		'$krrstub',
		Yup.object({
			epost: Yup.string(),
			gyldigFra: Yup.date().nullable(),
			landkode: Yup.mixed().when(['registrert', 'mobil'], {
				is: (registrert, mobil) => registrert && mobil,
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
