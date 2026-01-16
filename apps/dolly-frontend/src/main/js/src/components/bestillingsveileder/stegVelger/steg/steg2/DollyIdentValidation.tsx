import * as Yup from 'yup'
import { InntektstubForm } from '@/components/fagsystem/inntektstub/form/Form'
import { InntektsmeldingForm } from '@/components/fagsystem/inntektsmelding/form/Form'
import { AaregForm } from '@/components/fagsystem/aareg/form/Form'
import { BrregstubForm } from '@/components/fagsystem/brregstub/form/Form'
import { ArenaForm } from '@/components/fagsystem/arena/form/Form'
import { InstForm } from '@/components/fagsystem/inst/form/Form'
import { UdistubForm } from '@/components/fagsystem/udistub/form/Form'
import { PensjonForm } from '@/components/fagsystem/pensjon/form/Form'
import { MedlForm } from '@/components/fagsystem/medl/form/MedlForm'
import { OrganisasjonForm } from '@/components/fagsystem/organisasjoner/form/Form'
import { SkjermingForm } from '@/components/fagsystem/skjermingsregister/form/SkjermingForm'
import { ArbeidsplassenForm } from '@/components/fagsystem/arbeidsplassen/form/Form'
import { SigrunstubPensjonsgivendeForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import { KrrstubForm } from '@/components/fagsystem/krrstub/form/KrrForm'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/MalForm'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgGruppe'
import { SkattekortForm } from '@/components/fagsystem/skattekort/form/Form'
import { FullmaktForm } from '@/components/fagsystem/fullmakt/form/FullmaktForm'
import { YrkesskaderForm } from '@/components/fagsystem/yrkesskader/form/Form'
import { dokarkivValidation } from '@/components/fagsystem/dokarkiv/form/DokarkivValidation'
import { histarkValidation } from '@/components/fagsystem/histark/form/HistarkValidation'
import { ArbeidssoekerregisteretForm } from '@/components/fagsystem/arbeidssoekerregisteret/form/Form'
import { ifPresent } from '@/utils/YupValidations'
import { SigrunstubSummertSkattegrunnlagForm } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import { validation as sykdomValidation } from '@/components/fagsystem/sykdom/form/validation'
import { validation as pdlfValidation } from '@/components/fagsystem/pdlf/form/validation/validation'

export const DollyIdentValidation = Yup.object({
	antall: ifPresent(
		'$antall',
		Yup.number()
			.transform((v, o) => (o === '' ? null : v))
			.positive('Må være et positivt tall')
			.min(1, 'Må minst opprette 1 person')
			.max(50, 'Kan kun bestille max 50 identer om gangen.')
			.required('Oppgi antall personer'),
	),
	...pdlfValidation,
	...AaregForm.validation,
	...ArbeidsplassenForm.validation,
	...ArbeidssoekerregisteretForm.validation,
	...SigrunstubPensjonsgivendeForm.validation,
	...SigrunstubSummertSkattegrunnlagForm.validation,
	...InntektstubForm.validation,
	...InntektsmeldingForm.validation,
	...SkattekortForm.validation,
	...sykdomValidation,
	...YrkesskaderForm.validation,
	...BrregstubForm.validation,
	...InstForm.validation,
	...KrrstubForm.validation,
	...MedlForm.validation,
	...ArenaForm.validation,
	...UdistubForm.validation,
	...SkjermingForm.validation,
	...FullmaktForm.validation,
	...dokarkivValidation,
	...histarkValidation,
	...MiljoVelger.validation,
	...PensjonForm.validation,
	...MalForm.validation,
	...VelgGruppe.validation,
})
export const DollyOrganisasjonValidation = Yup.object({
	...OrganisasjonForm.validation,
	...MiljoVelger.validation,
	...MalForm.validation,
})
