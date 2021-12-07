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

export const TpsImportDiverse = ({ formikBag }) => {
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)
	const HarAktivSkjerming = () => {
		return personFoerLeggTil?.tpsMessaging?.hasOwnProperty('egenAnsattDatoFom')
			? personFoerLeggTil?.tpsMessaging?.hasOwnProperty('egenAnsattDatoTom')
				? isAfter(new Date(personFoerLeggTil?.tpsMessaging?.egenAnsattDatoTom), new Date())
				: true
			: false
	}

	const harSkjerming = HarAktivSkjerming()

	return (
		<React.Fragment>
			<FormikSelect
				name="tpsMessaging.sprakKode"
				label="Språk"
				kodeverk={PersoninformasjonKodeverk.Spraak}
				size="large"
				visHvisAvhuket
			/>

			<FormikDatepicker
				name="tpsMessaging.egenAnsattDatoFom"
				label="Skjerming fra"
				disabled={harSkjerming}
				visHvisAvhuket
			/>
			{harSkjerming && (
				<FormikDatepicker
					name="tpsMessaging.egenAnsattDatoTom"
					label="Skjerming til"
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
