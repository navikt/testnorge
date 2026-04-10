import { useFormContext, useWatch } from 'react-hook-form'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'
import { getOrgMiljoer } from '@/utils/OrgUtils'
import StyledAlert from '@/components/ui/alert/StyledAlert'

const extractOrgnummere = (formData: any): string[] => {
	const orgnummere: string[] = []

	formData?.aareg
		?.map((af: any) => af?.arbeidsgiver?.orgnummer)
		?.filter((orgnr: string | undefined) => orgnr?.length === 9)
		?.forEach((orgnr: string) => orgnummere.push(orgnr))

	formData?.inntektsmelding?.inntekter
		?.map((inntekt: any) => inntekt?.arbeidsgiver?.virksomhetsnummer)
		?.filter((orgnr: string | undefined) => orgnr?.length === 9)
		?.forEach((orgnr: string) => orgnummere.push(orgnr))

	formData?.inntektstub?.inntektsinformasjon
		?.map((info: any) => info?.virksomhet)
		?.filter((orgnr: string | undefined) => orgnr?.length === 9)
		?.forEach((orgnr: string) => orgnummere.push(orgnr))

	return [...new Set(orgnummere)]
}

export const OrgMiljoeVarsel = () => {
	const { control } = useFormContext()
	const aareg = useWatch({ name: 'aareg', control })
	const inntektsmelding = useWatch({ name: 'inntektsmelding', control })
	const inntektstub = useWatch({ name: 'inntektstub', control })
	const environments: string[] = useWatch({ name: 'environments', control }) || []

	const uniqueOrgnummere = extractOrgnummere({ aareg, inntektsmelding, inntektstub })

	const { organisasjoner, loading } = useOrganisasjonForvalter(uniqueOrgnummere)

	if (loading || uniqueOrgnummere.length === 0 || environments.length === 0) {
		return null
	}

	const warnings: string[] = []
	organisasjoner?.forEach((org) => {
		const orgMiljoer = getOrgMiljoer(org)
		if (orgMiljoer.length === 0) return

		const orgnummer = (org as Record<string, any>)[orgMiljoer[0]]?.organisasjonsnummer || ''

		const missingEnvs = environments
			.filter((env) => !orgMiljoer.includes(env))
			.map((env) => env.toUpperCase())

		if (missingEnvs.length > 0) {
			const envText = missingEnvs.join(', ')
			const suffix = missingEnvs.length === 1 ? 'det miljøet' : 'de miljøene'
			warnings.push(
				`Valgt organisasjon ${orgnummer} ikke funnet i ${envText}. Bestillingen vil muligens ikke fungere som den skal i ${suffix}.`,
			)
		}
	})

	if (warnings.length === 0) return null

	return (
		<>
			{warnings.map((warning, idx) => (
				<StyledAlert key={idx} variant="warning" size="small" style={{ marginTop: 10 }}>
					{warning}
				</StyledAlert>
			))}
		</>
	)
}
