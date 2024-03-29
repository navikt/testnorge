import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatStringDates } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

export const PermisjonPermitteringer = ({ data }) => {
	if (!data || data.length === 0) {
		return null
	}

	const permisjon = []
	const permittering = []

	data.forEach((periode) => {
		if (periode.type === 'permittering') {
			permittering.push(periode)
		} else {
			permisjon.push(periode)
		}
	})

	return (
		<>
			{permisjon.length > 0 && (
				<>
					<h4>Permisjon</h4>
					<ErrorBoundary>
						<DollyFieldArray data={permisjon} nested>
							{(id, idx) => (
								<div className="person-visning_content" key={idx}>
									<TitleValue
										title="Permisjonstype"
										value={id.type || id.beskrivelse}
										kodeverk={ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse}
									/>
									{id.periode && (
										<TitleValue title="Startdato" value={formatStringDates(id.periode.fom)} />
									)}
									{id.startdato && (
										<TitleValue title="Startdato" value={formatStringDates(id.startdato)} />
									)}
									{id.periode && (
										<TitleValue title="Sluttdato" value={formatStringDates(id.periode.tom)} />
									)}
									{id.sluttdato && (
										<TitleValue title="Startdato" value={formatStringDates(id.sluttdato)} />
									)}
									<TitleValue
										title="Permisjonsprosent"
										value={id.prosent || id.permisjonsprosent}
									/>
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				</>
			)}
			{permittering.length > 0 && (
				<>
					<h4>Permittering</h4>
					<ErrorBoundary>
						<DollyFieldArray data={permittering} nested>
							{(id, idx) => (
								<div className="person-visning_content" key={idx}>
									{id.periode && (
										<TitleValue title="Startdato" value={formatStringDates(id.periode.fom)} />
									)}
									{id.periode && (
										<TitleValue title="Sluttdato" value={formatStringDates(id.periode.tom)} />
									)}
									<TitleValue title="Permitteringsprosent" value={id.prosent} />
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				</>
			)}
		</>
	)
}
