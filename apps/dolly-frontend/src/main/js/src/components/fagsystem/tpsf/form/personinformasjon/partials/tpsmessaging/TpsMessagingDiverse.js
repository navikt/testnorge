import React, { useContext } from 'react'
import { isAfter } from 'date-fns'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { UtenlandskBankkonto } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/utenlandskbankkonto/UtenlandskBankkonto'
import { NorskBankkonto } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/norskbankkonto/NorskBankkonto'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

const bankkontoPath = ['tpsMessaging.utenlandskBankkonto', 'tpsMessaging.norskBankkonto']

export const TpsMessagingDiverse = ({ formikBag }) => {
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)
	const HarAktivSkjerming = () => {
		if (personFoerLeggTil?.tpsMessaging?.hasOwnProperty('egenAnsattDatoTom')) {
			return personFoerLeggTil?.tpsMessaging?.hasOwnProperty('egenAnsattDatoFom')
				? isAfter(new Date(personFoerLeggTil?.tpsMessaging?.egenAnsattDatoTom), new Date())
				: false
		} else {
			return personFoerLeggTil?.tpsMessaging?.hasOwnProperty('egenAnsattDatoFom')
		}
	}

	const settFormikDate = (value, path) => {
		formikBag.setFieldValue(`tpsMessaging.${path}`, value)
		formikBag.setFieldValue(`skjerming.${path}`, value)
	}

	const harSkjerming = HarAktivSkjerming()

	return (
		<React.Fragment>
			<FormikSelect
				name="tpsMessaging.spraakKode"
				label="Språk"
				kodeverk={PersoninformasjonKodeverk.Spraak}
				size="large"
				visHvisAvhuket
			/>

			<FormikDatepicker
				name="tpsMessaging.egenAnsattDatoFom"
				label="Skjerming fra"
				disabled={harSkjerming}
				onChange={(date) => {
					settFormikDate(date, 'egenAnsattDatoFom')
				}}
				visHvisAvhuket
			/>
			{harSkjerming && (
				<FormikDatepicker
					name="tpsMessaging.egenAnsattDatoTom"
					label="Skjerming til"
					onChange={(date) => {
						settFormikDate(date, 'egenAnsattDatoTom')
					}}
					visHvisAvhuket
				/>
			)}
			<Kategori title="Bankkonto" vis={bankkontoPath}>
				<UtenlandskBankkonto />
				<NorskBankkonto formikBag={formikBag} />
			</Kategori>
		</React.Fragment>
	)
}
