# Focus - Seu Assistente de Foco Inteligente

## 🎯 Descrição da Proposta

Vivemos na era da distração digital, onde a capacidade de manter a concentração em uma única tarefa é um superpoder. O **Focus** foi criado para ser mais do que um simples cronômetro; ele é um **assistente de produtividade inteligente** projetado para ajudar estudantes, profissionais e qualquer pessoa a retomar o controle de seu tempo e atenção.

O problema que resolvemos não é a falta de vontade de focar, mas sim o **hábito físico e neurológico** de pegar o celular a cada momento de tédio ou notificação.

A proposta central é transformar o ato de focar em um compromisso físico e intencional. Ao iniciar uma sessão, o usuário é incentivado a virar o celular com a tela para baixo. A partir daí, o aplicativo se torna um "vigia" que ajuda o usuário a manter seu compromisso, transformando o celular, a maior fonte de distração, em um aliado para a concentração.

## 🛠️ Recursos Utilizados

A inteligência do Focus é construída sobre uma base de tecnologias modernas e robustas, combinando os recursos nativos do Android com a flexibilidade da nuvem.

### Sensores Nativos do Android

O grande diferencial do aplicativo é o uso inteligente dos sensores do dispositivo para entender o contexto do usuário:

- **Sensor de Proximidade**: Utilizado para detectar quando o usuário vira o celular com a tela para baixo sobre uma superfície. Esta ação física inicia ou retoma a sessão de foco, criando um gatilho comportamental claro.
- **Acelerômetro**: Utilizado para medir o movimento do aparelho. Se o usuário pega o celular durante uma sessão ativa, o acelerômetro detecta essa "violação de foco", pausando o cronômetro e fornecendo um feedback sutil (vibração) para orientar o usuário de volta à sua tarefa.

### Ecossistema Google Firebase

Toda a infraestrutura de dados e autenticação do aplicativo é gerenciada pelo Firebase, garantindo escalabilidade, segurança e sincronização em tempo real.

- **Firebase Authentication**: Gerencia todo o ciclo de vida do usuário (cadastro, login, logout) e a atualização segura de dados de perfil, como nome, foto e senha.
- **Cloud Firestore**: Utilizado como nosso banco de dados NoSQL para armazenar todas as informações das sessões de foco, incluindo duração, status (planejada, em progresso, completada, abandonada) e estatísticas de interrupção.
- **Cloud Storage for Firebase**: Responsável por armazenar de forma segura e eficiente os arquivos de mídia dos usuários, como as fotos de perfil. As regras de segurança garantem que um usuário só possa modificar sua própria foto.

## 🎬 Vídeo Demonstrativo

Assista a uma demonstração completa do aplicativo em ação, mostrando o fluxo de cadastro, a criação de uma sessão de foco, a interação com os sensores e a visualização do perfil do usuário.

**OBS:** O aplicativo exibe uma notificação fixa de sessão ativa (não é possível ver no vídeo)

[ASSISTA](https://drive.google.com/file/d/1_zZt2pdf4MyT2vO-S2OZUz6jkbzOLST8/view?usp=sharing)
