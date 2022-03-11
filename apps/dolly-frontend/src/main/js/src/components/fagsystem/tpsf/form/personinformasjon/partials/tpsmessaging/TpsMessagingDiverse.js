import React from 'react'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { UtenlandskBankkonto } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/tpsmessaging/utenlandskbankkonto/UtenlandskBankkonto'
import { NorskBankkonto } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/tpsmessaging/norskbankkonto/NorskBankkonto'

const bankkontoPath = ['tpsMessaging.utenlandskBankkonto', 'tpsMessaging.norskBankkonto']

export const TpsMessagingDiverse = ({ formikBag }) => {
	return (
		<React.Fragment>
			<FormikSelect
				name="tpsMessaging.spraakKode"
				label="Språk"
				kodeverk={PersoninformasjonKodeverk.Spraak}
				size="large"
				visHvisAvhuket
			/>
			<Kategori title="Bankkonto" vis={bankkontoPath}>
				<UtenlandskBankkonto />
				<NorskBankkonto formikBag={formikBag} />
			</Kategori>
		</React.Fragment>
	)
}
