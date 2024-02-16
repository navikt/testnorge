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
import { KrrValidation } from '@/components/fagsystem/krrstub/form/KrrValidation'

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
		formMethods.trigger('krrstub')
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
									isClearable={false}
									label={'Landkode'}
									onChange={(option: Option) => {
										setLand(option?.value)
										formMethods.setValue('krrstub.landkode', option?.landkode || '')
										formMethods.setValue('krrstub.land', option?.value || '')
										formMethods.trigger('krrstub')
									}}
									size={'xlarge'}
								/>
								<DollyTextInput
									name="krrstub.mobil"
									label="Mobilnummer"
									value={mobilnummer}
									size={'medium'}
									onChange={(event) => {
										setMobilnummer(event.target.value || '')
										formMethods.setValue('krrstub.mobil', event.target.value || '')
										formMethods.trigger('krrstub')
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

KrrstubForm.validation = KrrValidation
