import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { FullmaktType } from '@/components/fagsystem/fullmakt/FullmaktType'
import { Fragment } from 'react'

type FullmaktProps = {
	idx: number
	fullmakt: FullmaktType
}

export const Fullmakt = ({ fullmakt, idx }: FullmaktProps) => {
	return (
		<Fragment key={idx}>
			<TitleValue title="Fullmektig" value={fullmakt?.fullmektig} />
			<TitleValue title="Fullmektigs navn" value={fullmakt?.fullmektigsNavn} />
			<TitleValue title="Gyldig fra" value={formatDate(fullmakt?.gyldigFraOgMed)} />
			<TitleValue title="Gyldig til" value={formatDate(fullmakt?.gyldigTilOgMed)} />
			{fullmakt?.omraade?.map((omraade, index) => (
				<TitleValue
					key={index}
					title={'Tema'}
					value={`${omraade?.tema} - ${omraade?.handling?.reduce((acc, curr) => {
						return acc + (acc ? ', ' : '') + curr.charAt(0) //Kommaseparert liste med første bokstav i tilganger
					}, '')}`}
				/>
			))}
			<TitleValue
				title="Registrert"
				value={fullmakt?.registrert && formatDate(fullmakt.registrert)}
			/>
			<TitleValue title="Kilde" value={fullmakt?.kilde} />
			<TitleValue title="Opphørt" value={oversettBoolean(fullmakt?.opphoert)} />
		</Fragment>
	)
}
