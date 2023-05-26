import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import styled from 'styled-components'
import { Medlemskapsperioder } from '@/components/fagsystem/medl/visning/Visning'

type Props = {
	medlemskapsperiode: Medlemskapsperioder
}

const H4 = styled.h4`
	width: 100%;
`

export default ({ medlemskapsperiode }: Props) => (
	<div className="person-visning_content">
		<TitleValue title="kilde" value={medlemskapsperiode.kilde} />
	</div>
)
