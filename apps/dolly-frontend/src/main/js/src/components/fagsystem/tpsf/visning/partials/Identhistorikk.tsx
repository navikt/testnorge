import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, formatKjonnToString } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

export const TpsfIdenthistorikk = ({ identhistorikk, visTittel = true }) => {
	if (!identhistorikk || identhistorikk.length < 1) return false

	return (
		<ErrorBoundary>
			<div>
				{visTittel && (
					<SubOverskrift label="Identhistorikk" iconKind="designsystem-identhistorikk" />
				)}
				{identhistorikk.map(({ aliasPerson, regdato }, idx) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
						<TitleValue title="Identtype" value={aliasPerson.identtype} />
						<TitleValue title="Ident" value={aliasPerson.ident} />
						<TitleValue title="Kjønn" value={formatKjonnToString(aliasPerson.kjonn)} />
						<TitleValue title="Utgått dato" value={formatDate(regdato)} />
					</div>
				))}
			</div>
		</ErrorBoundary>
	)
}
