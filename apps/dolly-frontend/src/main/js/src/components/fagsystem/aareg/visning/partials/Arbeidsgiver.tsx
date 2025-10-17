import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'

export const Arbeidsgiver = ({ data }) => {
	const { organisasjoner: virksomhetInfo } = useOrganisasjonForvalter([data.organisasjonsnummer])
	const { organisasjoner: opplysningspliktigInfo } = useOrganisasjonForvalter([
		data.opplysningspliktig,
	])
	if (!data || data.length === 0) {
		return null
	}

	const virksomhetNavn =
		virksomhetInfo?.[0]?.q1?.organisasjonsnavn || virksomhetInfo?.[0]?.q2?.organisasjonsnavn
	const opplysningspliktigNavn =
		opplysningspliktigInfo?.[0]?.q1?.organisasjonsnavn ||
		opplysningspliktigInfo?.[0]?.q2?.organisasjonsnavn

	return (
		<React.Fragment>
			<h4>Arbeidsgiver</h4>
			<div className="person-visning_content">
				<TitleValue title="Aktørtype" value={data.type} />
				{(data.type === 'Organisasjon' || data.type === 'ORGANISASJON') && (
					<>
						<TitleValue
							title="Virksomhet"
							value={`${data?.organisasjonsnummer} - ${virksomhetNavn}`}
						/>
						{data.opplysningspliktig && (
							<TitleValue
								title="Opplysningspliktig"
								value={`${data.opplysningspliktig} - ${opplysningspliktigNavn}`}
							/>
						)}
					</>
				)}
				{data.type === 'Person' && (
					<TitleValue title="Arbeidsgiverident" value={data.offentligIdent} />
				)}
			</div>
		</React.Fragment>
	)
}
