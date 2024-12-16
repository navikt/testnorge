import { LightAsync as SyntaxHighlighter } from 'react-syntax-highlighter'
import xml from 'react-syntax-highlighter/dist/esm/languages/hljs/xml'
import styled from 'styled-components'
import { defaultStyle } from 'react-syntax-highlighter/dist/esm/styles/hljs'

SyntaxHighlighter.registerLanguage('xml', xml)

const StyledSyntaxHighlighter = styled(SyntaxHighlighter)`
	font-size: 0.9em;
	width: 100%;
`

type Props = {
	xmlString: string
	wrapLongLines?: boolean
}

export default ({ xmlString, wrapLongLines = false }: Props) => (
	<StyledSyntaxHighlighter language={'xml'} wrapLongLines={wrapLongLines} style={defaultStyle}>
		{xmlString}
	</StyledSyntaxHighlighter>
)
